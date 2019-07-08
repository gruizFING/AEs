#!/bin/bash
## /bin/bash loop.sh nombreDeLFileConfdeSUMO directorioDeLaRed ArchivoTl ArchivoEdge ArchivoOutputTrip nombreDelArchivodeEmission     ArchivoGvR ArchivoTripsRes ArchivoNumVehRes ArchivoEmissRes numVehTotalesEnRou tiempoSimulacion no olvidarse de poner la / al final de cada directorio
# ejemplo: 
# /bin/bash loop.sh c2smalaga.sumo.cfg malaga-alameda/ tl-logic.add.xml output-tripinfos.xml ./ 200 250
sumo -c $1 -a $3,$4 --tripinfo-output $5 --no-warnings > /dev/null
awk -f $2$"processOutTLS.awk" $3 > $7
gvr=$(cat $7)
awk -f $2$"processOutSUMO.awk" $5  outNumVeh=$9 numVehTotal=${11} greenVsred=$gvr sim_time=${12} > $8
numVeh=$(cat $9)
awk -f $2$"processOutEmis.awk" $6 numVehArrived=$numVeh > ${10}
rm $3 $4 $5 $6 $7 $8 $9 
