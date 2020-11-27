package com.cloudbees.pdk.hen.procedures

import groovy.transform.AutoClone
import com.cloudbees.pdk.hen.*

@AutoClone
//generated
class RunCLI extends Procedure {

    static RunCLI create(Plugin plugin) {
        return new RunCLI(procedureName: 'Run CLI', plugin: plugin, )
    }


    RunCLI flush() {
        this.flushParams()
        return this
    }
    

    //Generated
    
    RunCLI arguments(String arguments) {
        this.addParam('arguments', arguments)
        return this
    }
    
    RunCLI config(String config) {
        this.addParam('config', config)
        return this
    }
    
    RunCLI env(String env) {
        this.addParam('env', env)
        return this
    }
    
    RunCLI serviceName(String serviceName) {
        this.addParam('serviceName', serviceName)
        return this
    }
    

    
}