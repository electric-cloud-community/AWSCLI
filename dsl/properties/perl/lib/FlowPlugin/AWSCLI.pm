package FlowPlugin::AWSCLI;
use strict;
use warnings;
use base qw/FlowPDF/;
use FlowPDF::Log;
use Data::Dumper;
use JSON;

# Feel free to use new libraries here, e.g. use File::Temp;

# Service function that is being used to set some metadata for a plugin.
sub pluginInfo {
    return {
        pluginName          => '@PLUGIN_KEY@',
        pluginVersion       => '@PLUGIN_VERSION@',
        configFields        => [ 'config' ],
        configLocations     => [ 'ec_plugin_cfgs' ],
        defaultConfigValues => {}
    };
}

# Auto-generated method for the connection check.
# Add your code into this method and it will be called when configuration is getting created.
# $self - reference to the plugin object
# $p - step parameters
# $sr - StepResult object
# Parameter: config
# Parameter: desc
# Parameter: region
# Parameter: authType
# Parameter: roleArn
# Parameter: credential
# Parameter: sessionToken_credential
# Parameter: checkConnectionResource

sub checkConnection {
    my ($self, $p, $sr) = @_;

    eval {
        # Use $configValues to check connection, e.g. perform some ping request
        # my $client = Client->new($configValues); $client->ping();
        my $command = $self->_wrapAuth();
        $command->addArguments("sts", "get-caller-identity");
        my $res = $self->_cli()->runCommand($command);
        if ($res->getCode() != 0) {
            die "Failed to execute sts get-caller-identity: code " . $res->getCode() . ", stderr: " . $res->getStderr();
        }
        logInfo($res->getStdout());
        1;
    } or do {
        my $err = $@;
        # Use this property to surface the connection error details in the CD server UI
        $sr->setOutcomeProperty("/myJob/configError", $err);
        $sr->apply();
        die $err;
    };
}
## === check connection ends ===

sub _cli {
    my ($self) = @_;

    unless ($self->{cli}) {
        my $cli = FlowPDF::ComponentManager->loadComponent('FlowPDF::Component::CLI', {
            workingDirectory => $ENV{COMMANDER_WORKSPACE}
        });
        $self->{cli} = $cli;
    }
    return $self->{cli};
}

sub _wrapAuth {
    my ($self) = @_;

    my $context = $self->getContext();

    my $configValues = $context->getConfigValues();

    my $awsCli = $configValues->getParameter('cliPath');

    $ENV{AWS_DEFAULT_OUTPUT} = 'json';

    my $authType = $configValues->getRequiredParameter('authType')->getValue();
    my $defRegion = $configValues->getRequiredParameter('region')->getValue();
    $ENV{AWS_DEFAULT_REGION} = $defRegion;
    if ($authType eq 'sts' || $authType eq 'basic' || $authType eq 'sessionToken') {
        my $cred = $configValues->getRequiredParameter('credential');
        my $clientId = $cred->getUserName();
        my $secret = $cred->getSecretValue();

        $ENV{AWS_ACCESS_KEY_ID} = $clientId;
        $ENV{AWS_SECRET_ACCESS_KEY} = $secret;

        if ($authType eq 'sts') {
            logInfo("Role assumption...");
            my $roleArn = $configValues->getRequiredParameter('roleArn');
            my $assumeCli = $self->_cli()->newCommand($awsCli);
            my $jobId =  $ENV{COMMANDER_JOBID};
            my $sessionName = $configValues->getParameter('sessionName');
            unless($sessionName) {
                $sessionName = "cd-awscli-plugin-session-$jobId";
            }
            else {
                $sessionName = $sessionName->getValue();
            }
            $assumeCli->addArguments('sts', 'assume-role', '--role-arn', $roleArn, '--role-session-name', $sessionName);
            my $response = $self->_cli()->runCommand($assumeCli);
            if ($response->getCode() != 0) {
                die "Failed to assume role $roleArn: " . $response->getStderr();
            }
            my $output = $response->getStdout();
            logTrace("Assume response $output");
            my $c = decode_json($output);
            $ENV{AWS_SESSION_TOKEN} = $c->{Credentials}->{SessionToken};
            $ENV{AWS_ACCESS_KEY_ID} = $c->{Credentials}->{AccessKeyId};
            $ENV{AWS_SECRET_ACCESS_KEY} = $c->{Credentials}->{SecretAccessKey};
        }
    }

    if ($authType eq 'sessionToken') {
        logInfo("Using session token...");
        my $cred = $configValues->getRequiredParameter('sessionToken_credential');
        my $secret = $cred->getSecretValue();

        $ENV{AWS_SESSION_TOKEN} = $secret;
    }

    my $command = $self->_cli()->newCommand($awsCli);
    if ($configValues->getParameter('debugLevel')->getValue() + 0 > 1) {
        logDebug("Turning on debug on aws cli");
        $command->addArguments('--debug');
    }
    if ($configValues->isParameterExists('globalFlags')) {
        my $globalRaw = $configValues->getParameter('globalFlags')->getValue();
        my @flags = split(/\n+/, $globalRaw);
        $command->addArguments(@flags);
    }
    if ($configValues->isParameterExists('envConfig')) {
        my $envRaw = $configValues->getParameter('envConfig')->getValue();
        _setEnv($envRaw);
    }
    return $command;
}


sub _setEnv {
    my ($raw) = @_;

    for my $pair (split(/\n/, $raw)) {
        chomp $pair;
        unless($pair) {
            next;
        }
        my ($key, $value) = split(/=/, $pair, 2);
        $ENV{$key} = $value;
        logInfo("Using environment variable $key = $value");
    }
}


sub _parseArguments {
    my ($arguments) = @_;

    my @args = ();
    my $isEscaping = 0;
    my $inQuotes = 0;
    my $running = "";
    for my $c (split(//, $arguments)) {
        if ($c eq '\\') {
            $isEscaping = 1;
        }
        elsif ($c =~ /'|"/ && !$isEscaping) {
            $inQuotes = !$inQuotes;
        }

        $isEscaping = 0;

        if ($c =~ /\s/ && !$inQuotes) {

            push @args, $running;
            $running = '';
        }
        else {
            $running .= $c;
        }
    }

    if ($running) {
        push @args, $running;
    }

    @args = grep { $_ } @args;
    @args = map { s/^['"]?//; s/['"]?$//; $_ } @args;
    return @args;
}


# Auto-generated method for the procedure Run CLI/Run CLI
# Add your code into this method and it will be called when step runs
# $self - reference to the plugin object
# $p - step parameters
# Parameter: config
# Parameter: command

# $sr - StepResult object
sub runCLI {
    my ($self, $p, $sr) = @_;

    my $cli = $self->_cli();

    my $command = $self->_wrapAuth();

    my $service = $p->{serviceName};
    $command->addArguments($service);

    my $argsRaw = $p->{arguments};
    logDebug("Got arguments: $argsRaw");
    my @arguments = _parseArguments($argsRaw);
    logDebug("Split arguments: ", \@arguments);
    for my $arg (@arguments) {
        chomp $arg;
        unless($arg) {
            next;
        }
        if ($arg =~ /^--/) {
            my ($key, $value) = split(/\s/, $arg, 2);
            $command->addArguments($key);
            if ($value) {
                $command->addArguments($value);
            }
        }
        else {
            $command->addArguments($arg);
        }
    }
    if ($p->{env}) {
        _setEnv($p->{env});
    }
    logDebug("Command: ", $command);
    my $res = $cli->runCommand($command);
    logDebug("Response:", $res);
    if ($res->getCode() != 0) {
        die "Failed to execute command: code " . $res->getCode() . ", error: " . $res->getStderr();
    }
    my $stdout = $res->getStdout();
    logInfo("Response: $stdout");
    $sr->setOutputParameter('output', $stdout);
    my $summary = length($stdout) > 200 ? substr($stdout, 0, 200) . '[TRUNCATED]' : $stdout;
    $sr->setJobStepSummary("Response: $summary");
}
## === step ends ===
# Please do not remove the marker above, it is used to place new procedures into this file.


1;
