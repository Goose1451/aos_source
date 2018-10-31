#!/bin/bash
x=`find . -mindepth 1 -maxdepth 1 -type d -printf '%f\n'`
for i in $x
do
#chmod 777 $i
ls -l
echo "for ------> $i"
#war_folder=$(echo $i | cut -d. -f1)
#mkdir -p ${war_folder}
#mv $i "./${war_folder}"
cd "${i}"
#jar -xf $i
#rm -rf $i
path_to_services="./WEB-INF/classes/properties/services.properties"
if [ "$i" == "ROOT" ];then
  path_to_services="./services.properties"
fi
sed -i 's/\.\./\/opt/g' "./WEB-INF/classes/log4j.properties"
sed -i "s/account\.soapservice\.url\.host=localhost/account\.soapservice\.url\.host=${ACCOUNT_IP}/g" ${path_to_services}
sed -i "s/=localhost/=${MAIN_IP}/g" ${path_to_services}
sed -i "s/account\.soapservice\.url\.port=8080/account\.soapservice\.url\.port=${ACCOUNT_PORT}/g" ${path_to_services}
sed -i "s/=8080/=${MAIN_PORT}/g" ${path_to_services}
sed -i 's/single\.machine\.deployment=true/single\.machine\.deployment=false/g' "${path_to_services}"
if [[ "$i" == "order" || "$i" == "catalog" ]];then
#sed -i 's/=validate/=create/g' "./WEB-INF/classes/properties/internal_config_for_env.properties"
   sed -i "s/=localhost/=${POSTGRES_IP}/g" ./WEB-INF/classes/properties/internal_config_for_env.properties
   sed -i "s/=5432/=${POSTGRES_PORT}/g" ./WEB-INF/classes/properties/internal_config_for_env.properties
fi
if [ "$reverse_proxy" == "true" ];then
  echo "using nginx--------"
  sed -i 's/reverse\.proxy=false/reverse\.proxy=true/g' "${path_to_services}"
  sed -i 's/single\.machine\.deployment=false/single\.machine\.deployment=true/g' "${path_to_services}"
fi
cd ..
done

cd ../