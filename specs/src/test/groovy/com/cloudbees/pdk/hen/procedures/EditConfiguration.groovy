package com.cloudbees.pdk.hen.procedures

import groovy.transform.AutoClone
import com.cloudbees.pdk.hen.*

@AutoClone
//generated
class EditConfiguration extends Procedure {

    static EditConfiguration create(Plugin plugin) {
        return new EditConfiguration(procedureName: 'EditConfiguration', plugin: plugin, credentials: [
            
            'credential': null,
            
            'sessionToken_credential': null,
            
        ])
    }


    EditConfiguration flush() {
        this.flushParams()
        return this
    }
    

    //Generated
    
    EditConfiguration authType(String authType) {
        this.addParam('authType', authType)
        return this
    }
    
    EditConfiguration checkConnection(boolean checkConnection) {
        this.addParam('checkConnection', checkConnection)
        return this
    }
    
    EditConfiguration checkConnectionResource(String checkConnectionResource) {
        this.addParam('checkConnectionResource', checkConnectionResource)
        return this
    }
    
    EditConfiguration cliPath(String cliPath) {
        this.addParam('cliPath', cliPath)
        return this
    }
    
    EditConfiguration config(String config) {
        this.addParam('config', config)
        return this
    }
    
    EditConfiguration debugLevel(String debugLevel) {
        this.addParam('debugLevel', debugLevel)
        return this
    }
    
    EditConfiguration desc(String desc) {
        this.addParam('desc', desc)
        return this
    }
    
    EditConfiguration envConfig(String envConfig) {
        this.addParam('envConfig', envConfig)
        return this
    }
    
    EditConfiguration globalFlags(String globalFlags) {
        this.addParam('globalFlags', globalFlags)
        return this
    }
    
    EditConfiguration region(String region) {
        this.addParam('region', region)
        return this
    }
    
    EditConfiguration roleArn(String roleArn) {
        this.addParam('roleArn', roleArn)
        return this
    }
    
    EditConfiguration sessionName(String sessionName) {
        this.addParam('sessionName', sessionName)
        return this
    }
    

    
    EditConfiguration credential(String user, String password) {
        this.addCredential('credential', user, password)
        return this
    }


    EditConfiguration credentialReference(String path) {
        this.addCredentialReference('credential', path)
        return this
    }
    
    EditConfiguration sessionToken_credential(String user, String password) {
        this.addCredential('sessionToken_credential', user, password)
        return this
    }


    EditConfiguration sessionToken_credentialReference(String path) {
        this.addCredentialReference('sessionToken_credential', path)
        return this
    }
    
}