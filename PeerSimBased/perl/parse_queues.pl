#! /usr/bin/perl
use strict;
use warnings;
use Statistics::Descriptive;
use File::Basename;

# script pour parser le fichiers queues.txt
# format ligne a parser : date	id:upload:download id:upload:download

my $prog = basename $0;
my $output = ".queues_stats.txt";

if($#ARGV !=0){
  print "\nUsage:$prog <directory to parse>";
}
my $dir = $ARGV[0];
opendir(my $fh,$dir)|| die "can't open ".$dir;
my @files = grep{/queues.txt/} readdir($fh);
closedir $fh;
foreach (@files) {
  if($_ !~/^(\d+)\./) {
    next;
  }
  print "parsing ".$_."\n";
  unlink $dir."/".$1.$output;
  my $outp =  $dir."/".$1.$output;
  my $statuploads = Statistics::Descriptive::Sparse->new();
  my $statsdownloads = Statistics::Descriptive::Sparse->new();
  open(my $in,$dir."/".$_) or die "erreur ouverture fichier";
  while(<$in>) {
    next if(/^#/);
    if(/^(\d+)\s+/){
       my $ligne = substr $', 0, 20;
      print $ligne."\n";
    }
  }
}
print "ok statistics";
