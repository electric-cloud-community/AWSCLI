package com.cloudbees.pdk.hen

import com.cloudbees.pdk.hen.procedures.RunCLI
import com.electriccloud.spec.PluginSpockTestSupport
import spock.lang.Shared

class RunCLISpec extends PluginSpockTestSupport {

    @Shared
    AWSCLI awscli

    @Shared
    RunCLI runCLI

    def setupSpec() {
        awscli = AWSCLI.create()

    }

    def setup() {
        runCLI = awscli.runCLI.flush()
    }

    def 'run some command'() {
        when:
        def r = runCLI.serviceName('sts').arguments("get-caller-identity").run()
        then:
        assert r.successful
        and:
        println r.jobStep.jobSteps.first().summary
    }

    def 'describe instances'() {
        when:
        def r = runCLI.serviceName("ec2").arguments("describe-instances\n--filters Name=tag-key,Values=Owner").run()
        then:
        assert r.successful
    }

    def 'use env'() {
        when:
        def r = runCLI.serviceName("ec2")
            .arguments("describe-instances\n--filters Name=tag-key,Values=Owner")
            .env("AWS_SECRET_ACCESS_KEY=wrong").run()
        then:
        assert !r.successful
    }

    def 'using smart parse'() {
        when:
        def r = runCLI.serviceName("ec2")
            .arguments("describe-instances --filters 'Name=tag-key, Values=Owner'")
            .run()
        then:
        assert r.successful
    }

    def 'create key pair dry run'() {
        setup:
        def configWithBasic = awscli.config.clone().authType("basic")
        def reconfigured = AWSCLI.createWithoutConfig()
        reconfigured.configure(configWithBasic)
        when:
        def r = reconfigured.runCLI.serviceName("ec2").arguments("create-key-pair\n--key-name value\n--dry-run").run()
        then:
        assert r.jobStep.jobSteps.first().summary =~ /CreateKeyPair operation: Request would have succeeded, but DryRun flag is set./
    }

    def 'with global flags'() {
        setup:
        def configWithGlobalFlags = awscli.config.clone().globalFlags('--no-verify-ssl')
        def reconfigured = AWSCLI.createWithoutConfig()
        reconfigured.configure(configWithGlobalFlags)
        when:
        def r = reconfigured.runCLI.serviceName('sts').arguments("get-caller-identity").run()
        then:
        assert r.successful
        assert r.jobLog =~ /--no-verify-ssl/
    }

}
