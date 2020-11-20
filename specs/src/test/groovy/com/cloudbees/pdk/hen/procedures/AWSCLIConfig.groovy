package com.cloudbees.pdk.hen.procedures

import groovy.transform.AutoClone
import com.cloudbees.pdk.hen.*

@AutoClone
//generated
class AWSCLIConfig extends Procedure {

    static AWSCLIConfig create(Plugin plugin) {
        return new AWSCLIConfig(procedureName: 'CreateConfiguration', plugin: plugin, credentials: [
            
            'credential': null,
            
            'sessionToken_credential': null,
            
        ])
    }

    //Generated
    
    AWSCLIConfig authType(String authType) {
        this.addParam('authType', authType)
        return this
    }
    
    AWSCLIConfig checkConnection(boolean checkConnection) {
        this.addParam('checkConnection', checkConnection)
        return this
    }
    
    AWSCLIConfig checkConnectionResource(String checkConnectionResource) {
        this.addParam('checkConnectionResource', checkConnectionResource)
        return this
    }
    
    AWSCLIConfig cliPath(String cliPath) {
        this.addParam('cliPath', cliPath)
        return this
    }
    
    AWSCLIConfig config(String config) {
        this.addParam('config', config)
        return this
    }
    
    AWSCLIConfig debugLevel(String debugLevel) {
        this.addParam('debugLevel', debugLevel)
        return this
    }
    
    AWSCLIConfig desc(String desc) {
        this.addParam('desc', desc)
        return this
    }
    
    AWSCLIConfig envConfig(String envConfig) {
        this.addParam('envConfig', envConfig)
        return this
    }
    
    AWSCLIConfig globalFlags(String globalFlags) {
        this.addParam('globalFlags', globalFlags)
        return this
    }
    
    AWSCLIConfig region(String region) {
        this.addParam('region', region)
        return this
    }
    
    AWSCLIConfig roleArn(String roleArn) {
        this.addParam('roleArn', roleArn)
        return this
    }
    
    AWSCLIConfig sessionName(String sessionName) {
        this.addParam('sessionName', sessionName)
        return this
    }
    

    
    AWSCLIConfig credential(String user, String password) {
        this.addCredential('credential', user, password)
        return this
    }


    AWSCLIConfig credentialReference(String path) {
        this.addCredentialReference('credential', path)
        return this
    }
    
    AWSCLIConfig sessionToken_credential(String user, String password) {
        this.addCredential('sessionToken_credential', user, password)
        return this
    }


    AWSCLIConfig sessionToken_credentialReference(String path) {
        this.addCredentialReference('sessionToken_credential', path)
        return this
    }
    
}