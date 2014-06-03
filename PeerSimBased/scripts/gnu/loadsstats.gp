set terminal pngcairo  enhanced
set xlabel "loads"
set ylabel "% nodes"

set key bottom
set xrange [0:350]
set yrange [0:100]
set title ""
system('mkdir -p png')
do for [ii=0:29]{
set output sprintf('png/animation%03.0f.png',ii)
plot "../results/SymetriqueBW10_5/PowerOfChoice/GLOBAL_LEAFSET_MTBF7DAYS_1TYPENODE_nodes200.filesize10000.10000files.3replicas.selection99/loadsstats0-20.txt" index ii using 1:2 s cumul title sprintf('day %02.0f',ii)
}
# creation du gif anime
system('convert -delay 20 -loop 1  png/*.png png/animated.gif')
