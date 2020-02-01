WordPress Example
==================

This example shows how you can configure zesty-router to serve WordPress via FastCGI. The first step is to have WordPress
installed on your server machine, for example under /var/www/wordpress. For more information about how to install WordPress,
please refer to the `WordPress Installation Guide <https://codex.wordpress.org/Installing_WordPress>`_.

This example assumes you are on a Linux system, but there will be a section later on for a Windows system as well. The points below are simply a checklist, and cannot by any measure replace the steps outlined in the wordpress documentation.

    * create a holder for the wordpress application :code:`sudo mkdir -p /var/www` with sudo permissions.
    * Assuming you have downloaded and unpacked the wordpress application into your home's Download folder, move the wordpress folder to the the *www* directory created in the previous step - :code:`sudo mv ~/Downloads/wordpress /var/www`.
    * Install :code:`php-fpm` in your machine using your system's package manager - :code:`sudo pacman -S php-fpm`. This is a *FastCGI Process Manager* for PHP which will bridge the wordpress application to :code:`zesty-router` application.
    * Configure :code:`php-fpm` to listen on a TCP port. In some systems, the default configuration is set to listen on a unix socket file which would not work in this case.::

        # Listen on localhost port 9000
        Listen 127.0.0.1:9000
        # Ensure only localhost can connect to PHP-FPM
        listen.allowed_clients = 127.0.0.1

    * A quick check using :code:`sudo php-fpm -t` should give an indication as to whether it is configured correctly.
    * Start the *php-fpm* process - :code:`sudo systemctl start php-fpm`. You can use *restart* if it was already running.
    * You could optionally choose to run *php-fpm* as a service as well if you haven't - :code:`sudo systemctl enable php-fpm`.
    * To verify that the process is running at any time, use :code:`sudo ps -ef | grep php-fpm`
    * Create a database for the wordpress application, say :code:`db_wordpress`'.
    * Configure your database settings in the :code:`wp-config.php` file.

With these steps covered on the wordpress side, let's now configure the :code:`zesty-router` side of the equation. Create your
application folder and in it create an :code:`app.js` file. Add this to your new file.::

    let zesty = Java.type('com.practicaldime.router.base.AppProvider');
    let app = zesty.provide({});

    let port = 8080, host = 'localhost';
    app.router()
        .wordpress("/var/www/wordpress", "http://127.0.0.1:9000")
        .listen(port, host, function(result){
            print(result);
        });

We created the app with an empty config to the :code:`AppProvider`, which implies that the root context is */*. We have *php-fpm*
listening on port 9000 and wordpress installed at :code:`/var/www/wordpress`. Fire up the app at this point.::

    jjs --language=es6 -ot -scripting -J-Dlogback.configurationFile=../lib/app-logback.xml \
    -J-Djava.class.path=../lib/zesty-router-0.1.0-shaded.jar app.js

When the app starts, navigate to http://localhost:8080/wp-admin/install.php to begin setting up wordpress.
