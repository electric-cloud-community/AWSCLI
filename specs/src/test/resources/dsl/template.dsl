def templateName = args.templateName
assert templateName
def projName = args.projectName ?: 'EC2 Specs Project'

project projName, {

    resourceTemplate templateName, {
        cfgMgrPluginKey = null
        cfgMgrProcedure = null
        cfgMgrProjectName = null
        cloudProviderParameter = args.parameters
        cloudProviderPluginKey = 'EC-AWS-EC2'
        cloudProviderProcedure = 'API_RunInstances'
        cloudProviderProjectName = null


        property 'ec_cloud_plugin_parameter', {

            // Custom properties
            args.parameters.each { k, v ->
                property k, value: v
            }

            property 'zone', value: args.parameters.zone, {
                expandable = '1'
            }
        }
    }

    environmentTemplate templateName, {

        environmentTemplateTier 'Tier 1', {
            resourceCount = '1'
            resourceTemplateName = templateName
            resourceTemplateProjectName = projName
        }

        // Custom properties

        property 'ec_deploy', {

            // Custom properties
            ec_usageCount = '0'
        }
    }

}