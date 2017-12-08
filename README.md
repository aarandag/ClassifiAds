# ClassifiAds

This project is a multi-agent system that intends to retrieve information from web to classify advertisements. The platform that has been used
is Jade, a framework implemented in Java language to simplify the implementation of multia-agent systems.

# Installation

First of all, it is important to know that the following installation is made in Eclipse. We begin with cloning or downloading the project using
the following command:
``
git clone https://github.com/aarandag/ClassifiAds.git
``
Then, we enter in Eclipse and open the ClassifiAds project. Once we have done this step, we go to:
``
ClassifiAds > Properties > Java Build Path > Libraries tab > Add externals jARs..
``
and search for lib folder and add all JARs.

After that, we must configure our run configuration. To do that, we follow these steps:
``
Run > Run Configurations > Main tab > Project
``
and browse ClassifiAds and click on Enter. Then, we go to ``Main class`` and type the following command:
``
jade.Boot
``
and click on ``Include system libraries when searching for a main class``.

Finally, we go to ``Arguments tab`` in ``Run Configurations..`` and type the following command:
``
-gui.
``
We click on Enter and we should run our program without difficulties.
