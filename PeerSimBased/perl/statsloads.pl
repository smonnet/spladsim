#!/usr/bin/perl
# calcul la moyenne du pourcentage de noeuds en fonction de la charge
# du jour 0 au jour  $end
# Le pourcentage est calcule pour un nombre de noeuds total de 200

use strict;

use File::Basename;
my $prog = basename $0;
#my %classloadstart;
#my %classloadend;
my ($load,$nodeid);
my $href;
my $end = 40;
if($#ARGV !=0){
  print "\nUsage:$prog <directory to parse>";
}
my $dir = $ARGV[0];
my @results = ();
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
    if(/^(\d+)/){
      my $day = $1;
      last if ($day == $end);
      if($filecmpt == 1){
	  push @results,{};
	}
      my  @tab = split;
      shift(@tab);
      foreach my $item (@tab)
      {
	($nodeid,$load) = split(':',$item);
	if( exists $results[$day]{$load} ) {
	  $results[$day]{$load}++;
	}else {
	  $results[$day]{$load} = 1;
	}
      }
    }
       
  }
}
#ecriture dans le fichier

open(my $fout, ">",$dir."/loadsstats0-20.txt") || die "erreur ouverture fichier sortie";
for $href (@results){
foreach $load (sort {$a <=> $b} keys (%$href)) {
  print $fout $load , "\t",($href->{$load})/(2*$filecmpt), "\n";
}
# deux lignes vides pour un nouveau dataset gnuplot
print $fout "\n\n";
}

