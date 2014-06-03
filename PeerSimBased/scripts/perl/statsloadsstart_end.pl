#!/usr/bin/perl
# calcule le pourcentage de noeuds en fonction de leur charge
# attention: le pourcentage est calculé en prenant en compte le nombre total de noeuds (200)
# le script serait a modifier si le nombre de noeuds changeait
use strict;

use File::Basename;
my $prog = basename $0;
my %classloadstart;
my %classloadend;
my ($load,$nodeid);

if($#ARGV !=0){
  print "\nUsage:$prog <directory to parse>";
}
my $dir = $ARGV[0];
# un compteur pour memoriser le nombre de fichiers traite
my $filecmpt = 0;
opendir(my $fh,$dir)|| die "can't open ".$dir;
my @files = grep{/loads.txt/} readdir($fh);
closedir $fh;
foreach (@files) {
  next if($_ !~/^(\d+)\./);
  $filecmpt++;
  open (my $in, $dir."/".$_) or die "erreur ouverture fichier";
  while(<$in>) {
    if(/^0/){
      my  @tab = split;
      shift(@tab);
      foreach my $item (@tab)
      {
	($nodeid,$load) = split(':',$item);
	if( exists $classloadstart{$load} ) {
	  $classloadstart{$load}++;
	}else {
	  $classloadstart{$load} = 1;
	}
      }
    }
    if(/^729/){
      my  @tab = split;
      shift(@tab);
      foreach my $item (@tab)
      {
	($nodeid,$load) = split(':',$item);
	if( exists $classloadend{$load} ) {
	  $classloadend{$load}++;
	}else {
	  $classloadend{$load} = 1;
	}
      }

    }
  }
}
#ecriture dans le fichier

open(my $fout, ">",$dir."/loadsstartend.txt") || die "erreur ouverture fichier sortie";
foreach $load (sort {$a <=> $b} keys (%classloadstart)) {
  print $fout $load , "\t",$classloadstart{$load}/(2*$filecmpt), "\n";
}
# deux lignes vides pour un nouveau dataset gnuplot
print $fout "\n\n";

foreach $load (sort {$a <=> $b} keys (%classloadend)) {
  print $fout $load , "\t",$classloadend{$load}/(2*$filecmpt), "\n";
}

