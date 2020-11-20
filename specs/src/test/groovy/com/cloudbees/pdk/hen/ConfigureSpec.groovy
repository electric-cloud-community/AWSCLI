package com.cloudbees.pdk.hen

import com.cloudbees.pdk.hen.procedures.AWSCLIConfig
import com.electriccloud.spec.PluginSpockTestSupport
import spock.lang.Shared
import spock.lang.Specification

class ConfigureSpec extends PluginSpockTestSupport {
    @Shared
    AWSCLI awscli

    @Shared
    AWSCLIConfig config

    def setup() {
        awscli = new AWSCLI()
        config = awscli.config.debugLevel('0').checkConnection(true)
    }

    def 'basic creds'() {
        when:
        String configName = randomize('test-basic-config')
        def r = config
            .config(configName)
            .authType("basic")
            .credential(AWSCLI.keyId(), AWSCLI.secret())
            .checkConnection(true)
            .region(AWSCLI.regionName())
            .runNaked()
        then:
        assert r.successful
        cleanup:
        awscli.deleteConfiguration(configName)
    }

    def 'assume role'() {
        when:
        String configName = randomize('test-assume-role')
        def r = config
            .config(configName)
            .authType('sts')
            .credential(AWSCLI.keyId(), AWSCLI.secret())
            .roleArn(AWSCLI.roleArn())
            .checkConnection(true)
            .region(AWSCLI.regionName())
            .runNaked()
        then:
        assert r.successful
        and:
        println r.jobLog
        assert r.jobLog =~ /cd-awscli-plugin-session/
    }

    def 'invalid creds'() {
        when:
        String configName = randomize('test-invalid-cionfig')
        def r = config
            .config(configName)
            .authType('basic')
            .credential('wrong', 'wrong')
            .checkConnection(true)
            .region(AWSCLI.regionName())
            .runNaked()
        then:
        assert !r.successful
        and:
        println r.jobProperties.configError
    }


}
