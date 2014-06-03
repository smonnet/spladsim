
set terminal pngcairo transparent enhanced font "arial,10" fontscale 1.0 size 500,400; set zeroaxis;;

set output "../../../Writings/2014-SRDS/Figures/test3D.png"
set ticslevel 0
set pm3d implicit interpolate 10,10
set ylabel "#files"
set zlabel "losses" rotate
set xlabel "selection range"
set yrange [2000:20000]
set ytics ("2000" 2000,"10000" 10000, "20000" 20000)
#set palette rgbformulae 3,11,6
#set palette defined (0 "royalblue",1 "turquoise",3 "yellow",5 "red")
splot "../results/PowerOfChoice/test3D.txt" using 1:2:3 title ''

set output "../../../Writings/2014-SRDS/Figures/testTaux3D.png"
set zlabel "loss ratio (%)" rotate
splot "../results/PowerOfChoice/test3Dtaux.txt" using 1:2:($3*100) title ""

