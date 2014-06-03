
set ylabel "losses ratio (%)"
set xlabel "data blocks number"
set terminal pngcairo transparent enhanced font "arial,10" fontscale 1.0; set zeroaxis;
set grid
set xtics 5000
set ytics
set key left top
#set style line 5 lt rgb "black"
set  output "../../../Writings/2014-SRDS/Figures/PowerOfChoiceTauxpertes2ans.png"
#set title " losses ratio (two years simulated)" 
set xrange [2000:20000]
list1 = "99 49 24 12 6 4"
list2 = "199 99 49 25 13 9" 

plot for[i=1:words(list1)] '../results/PowerOfChoice/sortedmergestatsTaux_selection'.word(list1,i).'.txt' using ($1):($2*100) with linespoints lc (i==6)?-1:i title 'selection range '.word(list2,i)

#set xrange [0:20000]
set output "../../../Writings/2014-SRDS/Figures/PowerOfChoiceTauxPertes2ans49_199.png"
plot for[i=1:3] '../results/PowerOfChoice/sortedmergestatsTaux_selection'.word(list1,i).'.txt'  using ($1):($2*100):($7*100):($8*100) with yerrorlines title 'selection range '.word(list2,i)


set ylabel "losses"
set output "../../../Writings/2014-SRDS/Figures/PowerOfChoicePertes2ans.png"
#set title "losses"
plot for[i=1:words(list1)] '../results/PowerOfChoice/sortedmergestats_selection'.word(list1,i).'.txt' using ($1):($2):($7):($8) with yerrorlines title 'selection range '.word(list2,i)


set output "../../../Writings/2014-SRDS/Figures/PowerOfChoicePertes2ans49_99.png"
plot for [i=1:3] '../results/PowerOfChoice/sortedmergestats_selection'.word(list1,i).'.txt'  using ($1):($2):($7):($8) with yerrorlines title 'selection range '.word(list2,i)

set key default
set xrange [7:200]
set xtics ("13" 13 ,"49" 49 ,"99" 99 ,"199" 199)
set output "../../../Writings/2014-SRDS/Figures/PowerOfChoiceSelectionRangevslosses.png"
#set ylabel "losses (%)"
set xlabel "selection range"
liste = '2000 3000 5000 6000 10000 13000'
plot for [j=0:5] "../results/PowerOfChoice/selectionRangevsLosses.txt" every :::j::j using 1:3 with lines title word(liste,j+1).' files'


#set title "losses 10 000 files"
set ylabel "losses"
set xtics ("9" 9,"25" 25 ,"49" 49 ,"99" 99 ,"199" 199)
set output "../../../Writings/2014-SRDS/Figures/selectionRangevslosses.png"
plot "../results/PowerOfChoice/selectionRangevsLosses.txt" every :::4::4 using 1:3 with lines title "Power Of Choice",\
"../results/PASTA/selectionvsloss.txt"  using 1:3 with lines title "Random",\
 "../results/LESSCHARGED/selectionvsloss.txt" using 1:3 with lines title "Less Charged"
# "../results/LESSCHARGEDMOD/selectionvsloss.txt" using 1:3 with lines title "less charged "

set xtics ("9" 9,"25" 25 ,"49" 49 ,"99" 99 ,"199" 199)
 #set yrange [0:90]
set output "../../../Writings/2014-SRDS/Figures/selectionRangevslosses_SymBW.png"
plot "../results/SymetriqueBW10_5/PowerOfChoice/selectionvsloss_MTBF7.txt" using (2*$1+1):3 with lines title "Power Of Choice",\
"../results/SymetriqueBW10_5/PASTA/selectionvsloss_MTBF7.txt" using (2*$1+1):3 with lines title "Random",\
 "../results/SymetriqueBW10_5/LESSCHARGED/selectionvsloss_MTBF7.txt" using (2*$1+1):3 with lines title "Less Charged"

