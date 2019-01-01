Zesty-Router Docs
=================

Zesty is a creative JavaScript wrapper around the Jetty web server. It works out-of-the-box with the servlet 3.0 API which enables handling requests in asynchronous fashion.
Note that the servlet 3.1 API on the other hand, introduces new interfaces to handle asynchronous socket I/O for which there isn't an idiomatic way to handle generically 
in zesty yet. However, using either Javascript or Java with zesty is handled using a uniform and easy to understand API.

The App Object
^^^^^^^^^^^^^^

The first thing you need to start using the zesty library is to get a hold of the AppProvider class::

    let zesty = Java.type('com.practicaldime.zesty.app.AppProvider');

.. toctree::
   :maxdepth: 2
    
   license
   help


Indices and tables
==================

* :ref:`genindex`
* :ref:`modindex`
* :ref:`search`
