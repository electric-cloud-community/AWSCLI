package com.cloudbees.pdk.hen

import com.cloudbees.pdk.hen.procedures.*
import com.cloudbees.pdk.hen.Plugin

import static com.cloudbees.pdk.hen.Utils.env

class AWSCLI extends Plugin {

    static AWSCLI create() {
        AWSCLI plugin = new AWSCLI(name: 'AWSCLI', defaultResource: 'awscli')
        createResource()
        plugin.configure(plugin.config)
        return plugin
    }
    static AWSCLI createWithoutConfig() {
        AWSCLI plugin = new AWSCLI(name: 'AWSCLI', defaultResource: 'awscli')
        createResource()
        return plugin
    }

    static private void createResource() {
        ServerHandler.getInstance().setupResource("awscli", env("RESOURCE"), env("RESOURCE_PORT") as int)
    }

    static String keyId() {
        env('AWS_ACCESS_KEY_ID')
    }

    static String secret() {
        env('AWS_SECRET_ACCESS_KEY')
    }

    static String regionName() {
        env('AWS_REGION_NAME')
    }

    static String roleArn() {
        'arn:aws:iam::372416831963:role/test-role-to-play-with-sts'
    }

    //user-defined after boilerplate was generated, default parameters setup
    AWSCLIConfig config = AWSCLIConfig
        .create(this)
        .authType('sts')
        .roleArn(roleArn())
        .credential(keyId(), secret())
        .region(regionName())
        .cliPath('/usr/local/bin/aws')

    RunCLI runCLI = RunCLI.create(this)

    EditConfiguration editConfiguration = EditConfiguration.create(this)

}