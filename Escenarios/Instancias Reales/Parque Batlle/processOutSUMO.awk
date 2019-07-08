BEGIN {FS="\"";ORS=" ";durationTotal=0;stops=0;
sub(/^  */, "", idle);numVeh=0}
/<tripinfo id=/, />/ {durationTotal+=$22;stops+=$26;numVeh++}
END{a=(durationTotal+((numVehTotal-numVeh)*sim_time)+stops);
b=(numVeh^2+greenVsred);
total=a/b;
print total;
print numVeh > outNumVeh;
}

