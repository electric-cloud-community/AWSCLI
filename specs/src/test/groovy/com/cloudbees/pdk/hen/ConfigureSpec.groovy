package com.cloudbees.pdk.hen

import com.cloudbees.pdk.hen.procedures.AWSCLIConfig
import com.electriccloud.spec.PluginSpockTestSupport
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.sts.StsClient
import software.amazon.awssdk.services.sts.model.AssumeRoleRequest
import software.amazon.awssdk.services.sts.model.AssumeRoleResponse
import spock.lang.Shared

class ConfigureSpec extends PluginSpockTestSupport {
    @Shared
    AWSCLI awscli

    @Shared
    AWSCLIConfig config

    def setup() {
        awscli = AWSCLI.createWithoutConfig()
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

    def 'assume role with session'() {
        when:
        String configName = randomize('test-assume-role')
        def r = config
            .config(configName)
            .authType('sts')
            .credential(AWSCLI.keyId(), AWSCLI.secret())
            .roleArn(AWSCLI.roleArn())
            .sessionName('my-session')
            .checkConnection(true)
            .region(AWSCLI.regionName())
            .runNaked()
        then:
        assert r.successful
        and:
        println r.jobLog
        assert r.jobLog =~ /my-session/
    }

    def 'session token'() {
        setup:
        String configName = randomize('test-session')
        def credentials = AwsBasicCredentials.create(
            AWSCLI.keyId(),
            AWSCLI.secret()
        )
        StsClient stsClient = StsClient
            .builder()
            .region(Region.of(AWSCLI.regionName()))
            .credentialsProvider(StaticCredentialsProvider.create(credentials))
            .build()
        String sessionName = 'test-session-id'
        AssumeRoleRequest request = AssumeRoleRequest.builder()
            .roleArn(AWSCLI.roleArn())
            .roleSessionName(sessionName)
            .build()
        AssumeRoleResponse response = stsClient.assumeRole(request)
        when:
        def r = config
            .config(configName)
            .authType('sessionToken')
            .credential(response.credentials().accessKeyId(), response.credentials().secretAccessKey())
            .sessionToken_credential("", response.credentials().sessionToken())
            .sessionName('my-session')
            .checkConnection(true)
            .region(AWSCLI.regionName())
            .runNaked()
        then:
        assert r.successful
        assert r.jobLog =~ /test-session-id/
        cleanup:
        awscli.deleteConfiguration(configName)
    }


    def 'invalid creds'() {
        when:
        String configName = randomize('test-invalid-config')
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
