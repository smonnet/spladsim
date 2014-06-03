#!/usr/bin/perl
use strict;
use warnings;
use File::Basename;

use Statistics::Descriptive;
# calcule les sttistiques sur la date de la perte du premier bloc
# calcule egalement le nombre d'occurence dans des intervalles de 30 jours
#declaration variables
my $prog = basename $0;
my $stat1 = Statistics::Descriptive::Full->new();
my $stat2 = Statistics::Descriptive::Full->new();

my @classes= ();
if($#ARGV != 0){
print "\nUsage: $prog directory";
}
my $dir = $ARGV[0];
opendir(my $fh,$dir)||die "can't open ".$dir;
my @files = grep {/churnPast.txt/} readdir($fh);
closedir $fh;
# variables pour les calculs de temps moyens
  my $percent1 = 0;
  my $numfiles = 0;

  open (my $resh,">",$dir."/firstlost.txt") || die 'can t open percent file';
  print $resh "# <time >\t<#blocks lost>\n";
foreach (@files) {
#  if($_ eq "merge.txt"){
#    next;
#  }
#print "opening ".$_."\n";
  open(my $in,$dir."/".$_) or die "erreur ouverture fichier";
  my $ligne = 0;
  $numfiles = 0;
  my $filelost = 0;
  # variable pour memoriser la classe courante de perte jour/30
  my $cl= 0;
  while(<$in>) {
	if(/^(\d+)\s+(\d+)/){
	  # initialiser les nombres de fichiers restant a surveiller
	  if($numfiles == 0){
	    $numfiles = $2;
	    $ligne++;
	    next;
	  }
	  if($2<$numfiles)
	  {
	    my $perte = $numfiles - $2;
	    print $resh "$ligne\t$perte\n";
	    $stat1->add_data($ligne);
	    $stat2->add_data($perte);
	    $filelost = 1;
	    $cl = $ligne/30;
	    if(exists($classes[$cl])){
		$classes[$cl]++;
	      }else{
		$classes[$cl]=1;
	      }
	    last;
	  }
	  $ligne++;
	}
  }
  if($filelost == 0) {
    print $resh "no loss detected\n";
  }
}
open (my $classrepartition,">",$dir."/firstlost_classes.txt")|| die 'can t open firstlost_classes.txt';
for (my $j = 0;$j<= $#classes; $j++){
  if(exists($classes[$j])){
    print $classrepartition "[".($j*30).";".(($j+1)*30)."[\t".$classes[$j]."\n";
  }else{
    print $classrepartition "[".($j*30).";".(($j+1)*30)."[\t0\n";
  }
} 
open (my $resttime,">",$dir."/firstlost_stat_time.txt") || die 'can t open percent file';
print $resttime "#stats time : <mean time(days)>\t<median>\t <standard_deviation> <intervalle confiance min> <intervalle confiance max>\t<min>\t<max>\n";
# calcul intervalle de confiance
my $interval = 1.96*$stat1->standard_deviation()/sqrt($stat1->count());
my $inf = $stat1->mean()-$interval;
my $max = $stat1->mean()+$interval;
print $resttime $stat1->mean()."\t".$stat1->median()."\t".$stat1->standard_deviation()."\t".$inf."\t".$max."\t".$stat1->min()."\t".$stat1->max()."\n";

open (my $restblocs,">",$dir."/firstlost_stat_num.txt") || die 'can t open percent file';
print $restblocs "# <mean loss>\t <standard_deviation>\n";
print $restblocs $stat2->mean()."\t".$stat2->standard_deviation()."\n";
