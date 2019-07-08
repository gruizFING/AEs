BEGIN {FS="\"";ORS=" ";CO_Total=0;CO2_Total=0;HC_Total=0;PMx_Total=0;NOx_Total=0;totalFuel=0;
sub(/^  */, "", idle)}
/CO_normed=/ {CO_Total+=$2;CO2_Total+=$4;HC_Total+=$6;PMx_Total+=$8;NOx_Total+=$10;totalFuel+=$12}
END{print CO_Total;
print CO2_Total;
print HC_Total;
print PMx_Total;
print NOx_Total;
print totalFuel;
print numVehArrived;
}
