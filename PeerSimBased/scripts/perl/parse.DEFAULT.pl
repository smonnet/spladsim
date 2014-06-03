#!/usr/bin/perl
use strict;
use warnings;
use File::Basename;

my %sum;
my %min;
my %max;
my %reference;
my $prog = basename $0;

if($#ARGV != 0){
print "\nUsage: $prog directory";
}
my $dir = $ARGV[0];
opendir(my $fh,$dir)||die "can't open ".$dir;
my @files = grep {/contChurn.txt/} readdir($fh);
closedir $fh;
foreach (@files) {
#  if($_ eq "merge.txt"){
#    next;
#  }
print "opening ".$_."\n";
  open(my $in,$dir."/".$_) or die "erreur ouverture fichier";
  while(<$in>) {
	if(/^(\d+)\s+(\d+)/){
	  if(!defined $min{$1})
	  {
		$min{$1} = $2;
		$max{$1}=$2
	  }else
	  {
		if($2 < $min{$1}){
		  $min{$1}=$2;
		}
		if($2>$max{$1}){
		  $max{$1}=$2;
		}
		$sum{$1} += $2;
		$reference{$1}++;
	  }
	}
  }
}
open ($fh,">",$dir."/merge.txt") || die 'cant open file';
my $time;
my $sum;
#while(($time,$sum) = each(%tableau)) {
#  print $fh $time."\t".$sum/$reference{$time}."\n";
#}
foreach $time (sort {$a <=>$b} keys %sum) {
 print $fh $time ."\t".$sum{$time}/$reference{$time}."\t".$min{$time}."\t".$max{$time}."\n";
}
close $fh;
