#!/usr/bin/perl
use strict;
use warnings;
use File::Basename;

use Statistics::Descriptive;

#declaration variables
my $prog = basename $0;
my $stat1 = Statistics::Descriptive::Sparse->new();
my $stat2 = Statistics::Descriptive::Sparse->new();
if($#ARGV != 0){
print "\nUsage: $prog directory";
}
my $dir = $ARGV[0];
opendir(my $fh,$dir)||die "can't open ".$dir;
my @files = grep {/churnPast.txt/} readdir($fh);
closedir $fh;
# variables pour les calculs de temps moyens
  my $percent1 = 0;
  my $percent2 = 0; 
  my $numfiles = 0;

  open (my $resh,">",$dir."/percents.txt") || die 'can t open percent file';
  print $resh "# <mean time 1% loss>\t<standard deviation>\t<mean time 2% loss>\t<standard deviation>\n";
foreach (@files) {
#  if($_ eq "merge.txt"){
#    next;
#  }
#print "opening ".$_."\n";
  open(my $in,$dir."/".$_) or die "erreur ouverture fichier";
  my $timepercent1 = 0;
  my $timepercent2 = 0;
  while(<$in>) {
	if(/^(\d+)\s+(\d+)/){
	  # initialiser les nombres de fichiers restant a surveiller
	  if($numfiles == 0){
	    $numfiles = $2;
	    # perte de 1%
	    $percent1 = $2-$2/100;
	    # print "reste a un pourcent ".$percent1."\n";
	    #perte de 2%
	    $percent2 = $percent1 - $2/100;
	  }
	  if($timepercent1 == 0 && $2<$percent1)
	  {
	    $timepercent1 = $1;
	    print $timepercent1."\n";
	    $stat1->add_data($1);
	  }
	  if($2 < $percent2)
	  {
	    $timepercent2 = $1;
	    $stat2->add_data($1);
	    last;
	  }

	}
  }
}
print $resh $stat1->mean()."\t".$stat1->standard_deviation()."\t".$stat2->mean()."\t".$stat2->standard_deviation()."\n";
