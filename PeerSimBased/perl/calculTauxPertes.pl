#! /usr/bin/perl
use strict;
use warnings;
use Statistics::Descriptive;
use Cwd;
# calcul les stats pour les taux de pertes
# il recoit en entree le nombre de fichiers restants a l'issue des simulations
# appele dans la commande:
#  cut -f 2 lastlines.txt |<path to>/calculTauxPertes.pl
#  le fichier lastlines.txt contient la derniere ligne des fichiers churnPast.txt
#
#il est concu pour etre executer dans le repertoire contenant les resultats de simulation
#
#sortie : une ligne de description des champs
#	  une ligne contenant les stats
#
my $workingdir = getcwd();
my $initial = 0;
if($workingdir =~/\d\.(\d+)files/)
{
  $initial = $1;
}
#print $initial."\n";
my $stat = Statistics::Descriptive::Sparse->new();
while(<>) {
  chomp();
  $stat->add_data(($initial -$_)/$initial);
}
my $coeff = 100*$stat->standard_deviation()/$stat->mean();
my $intervall = 1.96*$stat->standard_deviation()/sqrt($stat->count());
my $inf = $stat->mean()-$intervall;
my $sup = $stat->mean()+$intervall;
print "# nombre initial de fichiers  mean  standard_deviation min max cffvariation              \n"; 
print $initial."\t".$stat->mean()."\t".$stat->standard_deviation()."\t".$stat->min()."\t".$stat->max()."\t".$coeff."\t".$inf."\t".$sup."\n";
