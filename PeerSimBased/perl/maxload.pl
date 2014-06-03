#!/usr/bin/perl

use strict;

use Statistics::Descriptive;
use File::Basename;

my $stat = Statistics::Descriptive::Sparse->new();
my $prog = basename $0;
if($ARGV !=0){
  print "\nUsage:$prog <directory to parse>"
  }
my $dir = $ARGV[0];
# un compteur pour memorise le nombre de fichier traite
my $filecmpt = 0;
my $load = 0;
my $nodeid = 0;
opendir(my $fh,$dir)|| die "can't open ".$dir;
my @files = grep{/loads.txt/} readdir($fh);
closedir $fh;
foreach (@files) {
  next if($_ !~/^(\d+)\./);
  $filecmpt++;
  open (my $in, $dir."/".$_) or die "erreur ouverture fichier";
  while(<$in>) {
    
    if(/^(\d+)/){
      next if($1 < 100);
      chomp();
      my @tab = split;
      shift(@tab);
      # calcul de la valeur maximum pour la charge d un noeud
      my $max = 0;
      foreach my $item(@tab)
      {
	($nodeid,$load) = split(':',$item);
	if($load>$max){
	  $max = $load;
	}
      }
      # ajout de max dans l objet stat
      $stat->add_data($max);
    }
  }
}
  #affichage du resultat
print "#mean standard_deviation min max\n";
print $stat->mean(),"\t",$stat->standard_deviation(),"\t",$stat->min(),"\t",$stat->max(),"\n";

