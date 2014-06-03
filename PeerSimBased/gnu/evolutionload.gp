set terminal pngcairo  enhanced
set output "../../../Writings/2014-SRDS/Figures/evolutionloads_SR_199.png"
#set title "Evolution of load"
set xlabel "days"
set ylabel "load"
set xrange [0:30]
plot "../results/SymetriqueBW10_5/PowerOfChoice/GLOBAL_LEAFSET_MTBF7DAYS_1TYPENODE_nodes200.filesize10000.10000files.3replicas.selection99/evolutionloads.txt" using 1:2 with lines  title "Power Of Choice",\
"../results/SymetriqueBW10_5/PASTA/GLOBAL_LEAFSET_MTBF7DAYS_1TYPENODE_nodes200.filesize10000.10000files.3replicas.selection99/evolutionloads.txt" using 1:2 with lines title  "Random",\
"../results/SymetriqueBW10_5/LESSCHARGED/GLOBAL_LEAFSET_MTBF7DAYS_1TYPENODE_nodes200.filesize10000.10000files.3replicas.selection99/evolutionloads.txt" using 1:2  with lines title "Less Charged"

set output "../../../Writings/2014-SRDS/Figures/evolutionloads_SR_99.png"
plot "../results/SymetriqueBW10_5/PowerOfChoice/GLOBAL_LEAFSET_MTBF7DAYS_1TYPENODE_nodes200.filesize10000.10000files.3replicas.selection49/evolutionloads.txt" using 1:2 with lines  title "Power Of Choice",\
"../results/SymetriqueBW10_5/PASTA/GLOBAL_LEAFSET_MTBF7DAYS_1TYPENODE_nodes200.filesize10000.10000files.3replicas.selection49/evolutionloads.txt" using 1:2 with lines title  "Random",\
"../results/SymetriqueBW10_5/LESSCHARGED/GLOBAL_LEAFSET_MTBF7DAYS_1TYPENODE_nodes200.filesize10000.10000files.3replicas.selection49/evolutionloads.txt" using 1:2  with lines title "Less Charged"

