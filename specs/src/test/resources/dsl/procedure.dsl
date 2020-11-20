def projName = args.projectName
def procName = args.procedureName
def params = args.params

project projName, {
    procedure procName, {

        params.each { k, v ->
            formalParameter k, defaultValue: v, {
                type = 'textarea'
            }
        }

        step procName, {
            subproject = '/plugins/EC-AWS-EC2/project'
            subprocedure = procName

            params.each { k, v ->
                actualParameter k, '$[' + k + ']'
            }
        }
    }
}