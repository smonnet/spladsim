#!/usr/bin/perl

use strict;
use warnings;
use File::Basename;

# parse le fichier lastlines.txt  du repertoire donne en argument
# calcule pour chaque nombre de fichiers perdus le nombre de simulations correspondantes

# declaration des variables

my $prog = basename $0;
my @classes= ();
my $perte = 0;
if($#ARGV != 0){
  print "\nUsage: $prog directory";
}
my $dir = $ARGV[0];
open(my $fh,$dir."/lastlines.txt")||die "can't open ".$dir."/lastlines.txt";
while(<$fh>) {
  if(/^\d+\s+(\d+)/){
    $perte = 10000 - $1;
    if(exists($classes[$perte])){
      $classes[$perte]++;
    } else {
      $classes[$perte] = 1;
    }
  }
}
for (my $j = 0;$j<= $#classes; $j++){
  if(exists($classes[$j])){
    print $j."\t".$classes[$j]."\n";
  }else{

    print $j."\t0\n";
  }
} 

