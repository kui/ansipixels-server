ANSI Pixels Server
====================

A server for [ansi_pixels][]


Requirements
--------------

* Java 8


Installation
---------------

In terminal:

~~~~~~~~~~~~~~~~~~~~~~~~~~bash
$ ./scripts/gen-init.sh | sudo tee /etc/init.d/ansipixels
$ sudo chmod +x /etc/init.d/ansipixels
$ service ansipixels start
~~~~~~~~~~~~~~~~~~~~~~~~~~


[ansi_pixels]: https://kui.github.io/ansi_pixels
