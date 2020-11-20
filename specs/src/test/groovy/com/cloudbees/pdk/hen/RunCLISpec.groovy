package com.cloudbees.pdk.hen

import com.cloudbees.pdk.hen.procedures.AWSCLIConfig
import com.electriccloud.spec.PluginSpockTestSupport
import spock.lang.Shared

class RunCLISpec extends PluginSpockTestSupport {

    @Shared
    AWSCLI awscli

    def setupSpec() {
        awscli = AWSCLI.create()
    }

    def 'run some command'() {
        when:
        def r = awscli.runCLI.serviceName('sts').arguments("get-caller-identity").run()
        then:
        assert r.successful
        and:
        println r.jobStep.jobSteps.first().summary
    }

    def 'describe instances'() {
        when:
        def r = awscli.runCLI.serviceName("ec2").arguments("describe-instances\n--filters Name=tag-key,Values=Owner").run()
        then:
        assert r.successful
    }

    def 'with global flags'() {
        setup:
        def configWithGlobalFlags = awscli.config.clone().globalFlags('--no-verify-ssl')
        def reconfigured = new AWSCLI()
        reconfigured.configure(configWithGlobalFlags)
        when:
        def r = reconfigured.runCLI.serviceName('sts').arguments("get-caller-identity").run()
        then:
        assert r.successful
        assert r.jobLog =~ /--no-verify-ssl/
    }

}
