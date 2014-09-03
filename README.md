# New Relic Platform Apache httpd mod_status Plugin - Java



~~Find the New Relic Wikipedia Example plugin in the [New Relic storefront](http://newrelic.com/plugins/new-relic-inc/8)~~

~~Find the New Relic Wikipedia plugin in [Plugin Central](https://rpm.newrelic.com/extensions/com.newrelic.examples.wikipedia)~~

----


## Requirements

- A New Relic account. Sign up for a free account [here](http://newrelic.com)
- Java Runtime (JRE) environment Version 1.6 or later
- Network access to New Relic (authenticated proxies are not currently supported, see details below)

----

## Installation

This plugin can be installed one of the following ways:

* ~~[Option 1 - New Relic Platform Installer](#option-1--install-with-the-new-relic-platform-installer)~~
* ~~[Option 2 - Chef and Puppet Install Scripts](#option-2--install-via-chef-or-puppet)~~
* [Option 3 - Manual Install](#option-3--install-manually)

### Install Manually (Non-standard)

#### Step 1 - Downloading and Extracting the Plugin

The latest version of the plugin can be downloaded [here](???).  Once the plugin is on your box, extract it to a location of your choosing.

**note** - This plugin is distributed in tar.gz format and can be extracted with the following command on Unix-based systems (Windows users will need to download a third-party extraction tool or use the [New Relic Platform Installer](https://discuss.newrelic.com/t/getting-started-with-the-platform-installer/842)):

```
	tar -xvzf newrelic_wikipedia_plugin-X.Y.Z.tar.gz
```

#### Step 2 - Configuring the Plugin

Check out the [configuration information](#configuration-information) section for details on configuring your plugin. 

#### Step 3 - Running the Plugin

To run the plugin, execute the following command from a terminal or command window (assuming Java is installed and on your path):

```
	java -Xmx128m -jar plugin.jar
```

**Note:** Though it is not necessary, the '-Xmx128m' flag is highly recommended due to the fact that when running the plugin on a server class machine, the `java` command will start a JVM that may reserve up to one quarter (25%) of available memory, but the '-Xmx128m' flag will limit heap allocation to a more reasonable 128MBs.  

For more information on JVM server class machines and the `-Xmx` JVM argument, see: 

 - [http://docs.oracle.com/javase/6/docs/technotes/guides/vm/server-class.html](http://docs.oracle.com/javase/6/docs/technotes/guides/vm/server-class.html)
 - [http://docs.oracle.com/cd/E22289_01/html/821-1274/configuring-the-default-jvm-and-java-arguments.html](http://docs.oracle.com/cd/E22289_01/html/821-1274/configuring-the-default-jvm-and-java-arguments.html)
 
#### Step 4 - Keeping the Plugin Running

Step 3 showed you how to run the plugin; however, there are several problems with running the process directly in the foreground (For example, when the machine reboots the process will not be started again).  That said, there are several common ways to keep a plugin running, but they do require more advanced knowledge or additional tooling.  We highly recommend considering using the [New Relic Platform Installer](https://discuss.newrelic.com/t/getting-started-with-the-platform-installer/842) or Chef/Puppet scripts for installing plugins as they will take care of most of the heavy lifting for you.  

If you prefer to be more involved in the maintaince of the process, consider one of these tools for managing your plugin process (bear in mind that some of these are OS-specific):

- [Upstart](http://upstart.ubuntu.com/)
- [Systemd](http://www.freedesktop.org/wiki/Software/systemd/)
- [Runit](http://smarden.org/runit/)
- [Monit](http://mmonit.com/monit/)

----

## Configuration Information

### Configuration Files

You will need to modify two configuration files in order to set this plugin up to run.  The first (`newrelic.json`) contains configurations used by all Platform plugins (e.g. license key, logging information, proxy settings) and can be shared across your plugins.  The second (`plugin.json`) contains data specific to each plugin such as a list of hosts and port combination for what you are monitoring.  Templates for both of these files should be located in the '`config`' directory in your extracted plugin folder. 

#### Configuring the `plugin.json` file: 

The `plugin.json` file has a provided template in the `config` directory named `plugin.template.json`.  If you are installing manually, make a copy of this template file and rename it to `plugin.json` (the New Relic Platform Installer will automatically handle creation of configuration files for you).  

Below is an example of the `plugin.json` file's contents, you can add multiple objects to the "agents" array to monitor different instances:

```
{
  "agents": [
    {
      "name" : "Apache offical",
      "host" : "apache.org",
      "metrics" : [
          {"name":"Total Accesses", "unit":"#" },
          {"name":"Total kBytes", "unit":"#" },
          {"name":"CPULoad", "unit":"#" },
          {"name":"Uptime", "unit":"#" },
          {"name":"ReqPerSec", "unit":"#" },
          {"name":"BytesPerSec", "unit":"#" },
          {"name":"BytesPerReq", "unit":"#" },
          {"name":"BusyWorkers", "unit":"#" },
          {"name":"IdleWorkers", "unit":"#" },
          {"name":"ConnsTotal", "unit":"#" },
          {"name":"ConnsAsyncWriting", "unit":"#" },
          {"name":"ConnsAsyncKeepAlive", "unit":"#" },
          {"name":"ConnsAsyncClosing", "unit":"#" }
      ]
    }
  ]
}
```

**note** - The "name" attribute is used to identify specific instances in the New Relic UI. 

#### Configuring the `newrelic.json` file: 

The `newrelic.json` file also has a provided template in the `config` directory named `newrelic.template.json`.  If you are installing manually, make a copy of this template file and rename it to `newrelic.json` (again, the New Relic Platform Installer will automatically handle this for you).  

The `newrelic.json` is a standardized file containing configuration information that applies to any plugin (e.g. license key, logging, proxy settings), so going forward you will be able to copy a single `newrelic.json` file from one plugin to another.  Below is a list of the configuration fields that can be managed through this file:

##### Configuring your New Relic License Key

Your New Relic license key is the only required field in the `newrelic.json` file as it is used to determine what account you are reporting to.  If you do not know what your license key is, you can learn about it [here](https://newrelic.com/docs/subscriptions/license-key).

Example: 

```
{
  "license_key": "YOUR_LICENSE_KEY_HERE"
}
```

##### Logging configuration

By default Platform plugins will have their logging turned on; however, you can manage these settings with the following configurations:

`log_level` - The log level. Valid values: [`debug`, `info`, `warn`, `error`, `fatal`]. Defaults to `info`.

`log_file_name` - The log file name. Defaults to `newrelic_plugin.log`.

`log_file_path` - The log file path. Defaults to `logs`.

`log_limit_in_kbytes` - The log file limit in kilobytes. Defaults to `25600` (25 MB). If limit is set to `0`, the log file size would not be limited.

Example:

```
{
  "license_key": "YOUR_LICENSE_KEY_HERE"
  "log_level": "debug",
  "log_file_path": "/var/logs/newrelic"
}
```

##### Proxy configuration

If you are running your plugin from a machine that runs outbound traffic through a proxy, you can use the following optional configurations in your `newrelic.json` file:

`proxy_host` - The proxy host (e.g. `webcache.example.com`)

`proxy_port` - The proxy port (e.g. `8080`).  Defaults to `80` if a `proxy_host` is set

`proxy_username` - The proxy username

`proxy_password` - The proxy password

Example:

```
{
  "license_key": "YOUR_LICENSE_KEY_HERE",
  "proxy_host": "proxy.mycompany.com",
  "proxy_port": 9000
}
```

----

## Support

Plugin support and troubleshooting assistance can be obtained by visiting [support.newrelic.com](https://support.newrelic.com)

### Frequently Asked Questions

**Q: I've started this plugin, now what?**

**A:** Once you have a plugin reporting with the proper license key, log into New Relic [here](http://rpm.newrelic.com).  If everything was successful, you should see a new navigation item appear on the left navigation bar identifying your new plugin (This may take a few minutes).  Click on this item to see the metrics for what you were monitoring (bear in mind, some details -- such as summary metrics -- may take several minutes to show values).

----

