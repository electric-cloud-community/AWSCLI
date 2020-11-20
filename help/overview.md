<p>Amazon Elastic Compute Cloud (Amazon EC2) is a web service that provides resizable compute capacity in the cloud. It is designed
            to make web-scale computing easier for developers. EC2 allows users to rent virtual computers on which to run their computer
            applications.</p>

<p>It provides you with complete control of your computing resources and lets you run on the proven Amazon computing environment. Amazon EC2
            reduces the time required to obtain and boot new server instances to minutes, allowing you to quickly scale capacity, both up and down,
            as your computing requirements change. You can create, launch, and terminate server instances as needed, paying by the hour for active
servers. EC2 provides developers the tools to build failure resilient applications and isolate thems from common failure scenarios.</p>

<p>Amazon EC2 presents a true virtual computing environment, allowing you to use web service interfaces to launch instances with a
            variety of operating systems, load them with your custom application environment, manage your network access permissions, and run
            your image using as many or few systems as you want.</p>

<p>For more information about Amazon EC2, go to the <a href="http://aws.amazon.com/ec2/" target="_blank">Amazon Web Services website</a>.</p>
            <p>You can view the documentation at <a href="http://aws.amazon.com/documentation/ec2/" target="_blank">EC2</a>.</p>

## Differences from EC-EC2

This plugin is using Java SDK 2 [AWS SDK for Java 2.0 Developer Guide](https://docs.aws.amazon.com/sdk-for-java/v2/developer-guide/welcome.html) (the older plugin is using an outdated Perl API).

This plugin has procedures for provisioning and tearing down resources. This plugin does __not__ have procedures for
handling elastic IP address, VPC networks and dynamic resource pool scaling.