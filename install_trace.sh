#!/bin/bash
docker exec -it $( docker ps -a | grep "performancetesting/aos-accountservice-dev" | awk '{print $1}' ) bash -c 'unzip HPEAppPulseJava_1.60_AdvantageOnlineShopping.zip; AppPulseJavaAgent/lib; java -jar AppPulseAgent.jar setup'