set terminal pngcairo  enhanced
set output "../results/Figures/loadsstartend_SR_199_PowerOfChoice.png"
set title "PowerOfChoice"
set xlabel "loads"
set ylabel "% nodes"
plot "../results/SymetriqueBW10_5/PowerOfChoice/GLOBAL_LEAFSET_MTBF7DAYS_1TYPENODE_nodes200.filesize10000.10000files.3replicas.selection99/loadsstartend.txt" index 0 using 1:2 title "ditribution at day 0" ,\
"../results/SymetriqueBW10_5/PowerOfChoice/GLOBAL_LEAFSET_MTBF7DAYS_1TYPENODE_nodes200.filesize10000.10000files.3replicas.selection99/loadsstartend.txt" index 1 using 1:2 title "distribution at day 729"


set output "../results/Figures/loadsstartend_SR_199_Random.png"
set title "Random"
plot "../results/SymetriqueBW10_5/PASTA/GLOBAL_LEAFSET_MTBF7DAYS_1TYPENODE_nodes200.filesize10000.10000files.3replicas.selection99/loadsstartend.txt" index 0 using 1:2 title "ditribution at day 0" ,\
"../results/SymetriqueBW10_5/PASTA/GLOBAL_LEAFSET_MTBF7DAYS_1TYPENODE_nodes200.filesize10000.10000files.3replicas.selection99/loadsstartend.txt" index 1 using 1:2 title "distribution at day 729"


set output "../results/Figures/loadsstartend_SR_199_LESSCHARGED.png"
set title "LESSCHARGED"
plot "../results/SymetriqueBW10_5/LESSCHARGED/GLOBAL_LEAFSET_MTBF7DAYS_1TYPENODE_nodes200.filesize10000.10000files.3replicas.selection99/loadsstartend.txt" index 0 using 1:2 title "ditribution at day 0" ,\
"../results/SymetriqueBW10_5/LESSCHARGED/GLOBAL_LEAFSET_MTBF7DAYS_1TYPENODE_nodes200.filesize10000.10000files.3replicas.selection99/loadsstartend.txt" index 1 using 1:2 title "distribution at day 729"

set yrange [0:100]
set title "Random"
set xrange [0:1600]
set output "../results/Figures/cumulativeloads_SR_199_RANDOM.png"
plot "../results/SymetriqueBW10_5/PASTA/GLOBAL_LEAFSET_MTBF7DAYS_1TYPENODE_nodes200.filesize10000.10000files.3replicas.selection99/loadsstartend.txt" index 0 using 1:2 s cumul title "ditribution at day 0" ,\
"../results/SymetriqueBW10_5/PASTA/GLOBAL_LEAFSET_MTBF7DAYS_1TYPENODE_nodes200.filesize10000.10000files.3replicas.selection99/loadsstartend.txt" index 1 using 1:2 s cumul title "distribution at day 729"

set key bottom
set xrange [0:350]
set title "Less Charged"
set output "../results/Figures/cumulativeloads_SR_199_LESSCHARGED.png"
plot "../results/SymetriqueBW10_5/LESSCHARGED/GLOBAL_LEAFSET_MTBF7DAYS_1TYPENODE_nodes200.filesize10000.10000files.3replicas.selection99/loadsstartend.txt" index 0 using 1:2 s cumul title "ditribution at day 0" ,\
"../results/SymetriqueBW10_5/LESSCHARGED/GLOBAL_LEAFSET_MTBF7DAYS_1TYPENODE_nodes200.filesize10000.10000files.3replicas.selection99/loadsstartend.txt" index 1 using 1:2 s cumul title "distribution at day 729"
set title "PowerOfChoice"
set output "../results/Figures/cumulativeloads_SR_199_PowerOfChoice.png"
plot "../results/SymetriqueBW10_5/PowerOfChoice/GLOBAL_LEAFSET_MTBF7DAYS_1TYPENODE_nodes200.filesize10000.10000files.3replicas.selection99/loadsstartend.txt" index 0 using 1:2 s cumul title "ditribution at day 0" ,\
"../results/SymetriqueBW10_5/PowerOfChoice/GLOBAL_LEAFSET_MTBF7DAYS_1TYPENODE_nodes200.filesize10000.10000files.3replicas.selection99/loadsstartend.txt" index 1 using 1:2 s cumul title "distribution at day 729"


set key bottom
set xrange [0:350]
set output "../../../Writings/2014-SRDS/Figures/CDFloads_SR_199.png"
set title ""
plot "../results/SymetriqueBW10_5/PowerOfChoice/GLOBAL_LEAFSET_MTBF7DAYS_1TYPENODE_nodes200.filesize10000.10000files.3replicas.selection99/loadsstartend.txt" index 1 using 1:2 s cumul title "Power Of Choice  day 729",\
"../results/SymetriqueBW10_5/PASTA/GLOBAL_LEAFSET_MTBF7DAYS_1TYPENODE_nodes200.filesize10000.10000files.3replicas.selection99/loadsstartend.txt" index 1 using 1:2 s cumul title "Random  day 729",\
"../results/SymetriqueBW10_5/LESSCHARGED/GLOBAL_LEAFSET_MTBF7DAYS_1TYPENODE_nodes200.filesize10000.10000files.3replicas.selection99/loadsstartend.txt" index 1 using 1:2 s cumul title "Less Charged day 729",\
 "../results/SymetriqueBW10_5/LESSCHARGED/GLOBAL_LEAFSET_MTBF7DAYS_1TYPENODE_nodes200.filesize10000.10000files.3replicas.selection99/loadsstartend.txt" index 0 using 1:2 s cumul title "day 0" 

