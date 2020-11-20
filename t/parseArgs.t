#!/usr/bin/perl
use strict;
use warnings;
use Test::More;
use FlowPlugin::AWSCLI;
use Data::Dumper;

my $string = qq/aws sts get-caller-identity/;
my @args = FlowPlugin::AWSCLI::_parseArguments($string);
is(join(":", @args), 'aws:sts:get-caller-identity');

$string = q/aws ec2 describe-instances --instanceId i-asdasd --filters "Name=tag-value, Values=my-team"/;
@args = FlowPlugin::AWSCLI::_parseArguments($string);
is(join(":", @args), 'aws:ec2:describe-instances:--instanceId:i-asdasd:--filters:Name=tag-value, Values=my-team');


done_testing();

