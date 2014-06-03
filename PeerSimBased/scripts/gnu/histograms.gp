set terminal pngcairo  enhanced font "arial,10" fontscale 1.0 size 1000, 500
set output "histogram.png"
set style data histogram
set style fill solid
set style histogram clustered gap 1
set xrange [-1:*]
#plot '../results/FirstLoss/PowerOfChoice/GLOBAL_LEAFSET_MTBF7DAYS_1TYPENODE_nodes200.filesize10000.10000files.3replicas.selection99/firstlost_classes.txt' using 2 title "selection intervalle 199",\
#'../results/FirstLoss/PowerOfChoice/GLOBAL_LEAFSET_MTBF7DAYS_1TYPENODE_nodes200.filesize10000.10000files.3replicas.selection49/firstlost_classes.txt' using 2 title " selection intervalle 99"

#set yrange [0:10]
#set output "histogramMTBF14DAYS.png"
#plot '../results/FirstLoss/PowerOfChoice/GLOBAL_LEAFSET_MTBF14DAYS_1TYPENODE_nodes200.filesize10000.10000files.3replicas.selection49/firstlost_classes.txt' using 2 title "selection intervalle 99"

#set terminal pngcairo  enhanced font "arial,10" fontscale 1.0 size 500, 300
#set ylabel "pertes"
#set output "../results/Figures/histogrammeComparatifBW.png"
#set title "pertes a 2 ans 'selection range de 199"
#plot '../results/SymetriqueBW/histogram.txt' using 2:xtic(1) title "Symetrique BW (5/5 Mb)",\
#'../results/histogram.txt' using 2  title "asymetrique BW(1/10 Mb)"

#set output "../results/Figures/histogrammesPertes.png"
#set title "nombre de runs par nombre de pertes ( temps de simulation 2ans)"
#set xlabel "#pertes"
#set ylabel "#runs"

#plot '../results/SymetriqueBW/PowerOfChoice/GLOBAL_LEAFSET_MTBF7DAYS_1TYPENODE_nodes200.filesize10000.10000files.3replicas.selection99/classesPertes.txt' using 2 title 'PowerOfChoice',\
#'../results/SymetriqueBW/PASTA/GLOBAL_LEAFSET_MTBF7DAYS_1TYPENODE_nodes200.filesize10000.10000files.3replicas.selection99/classesPertes.txt' using 2 title 'RANDOM',\
#'../results/SymetriqueBW/LESSCHARGED/GLOBAL_LEAFSET_MTBF7DAYS_1TYPENODE_nodes200.filesize10000.10000files.3replicas.selection99/classesPertes.txt' using 2 title 'LESSCHARGED'


set terminal pngcairo  enhanced font "arial,10" fontscale 1.0 size 640, 480
set ylabel "losses"
set key left
set output "../results/Figures/histogramComparatifBW_SR199.png"
set title " "
set yrange [0:17]
set style fill pattern 0 border
plot '../results/SymetriqueBW10_5/histogram.txt' using 2:xtic(1) title "Symmetric BW (5.5/5.5 Mb)" lt -1,\
'../results/histogram.txt' using 2  title "Asymmetric BW(1/10 Mb)" lt -1

set output "../results/Figures/histogramComparatifBW_SR99.png"

plot '../results/SymetriqueBW10_5/histogram49.txt' using 2:xtic(1) title "Symmetric BW (5.5/5.5 Mb)" lt -1,\
'../results/histogram49.txt' using 2  title "Asymmetric BW(1/10 Mb)" lt -1


