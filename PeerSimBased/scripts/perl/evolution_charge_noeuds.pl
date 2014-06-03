#!/usr/bin/perl

use strict;
use File::Basename;

my $prog = basename $0;
#la structure pour memoriser les differentes charges
my %nodes=();

if($#ARGV !=0){
  print "\nUsage:$prog <directory to parse>";
}
my $dir = $ARGV[0];
my $maxid;
my ($id,$load);
#opendir(my $fh,$dir)|| die "can't open ".$dir;
#my @files = grep{/loads.txt/} readdir($fh);
#closedir $fh;
#foreach (@files) {
#  next if($_ !~/^(\d+)\./);
open (my $in, $dir."/0.loads.txt") or die "erreur ouverture fichier";

while(<$in>) {
  my @tab = split;
  my $day = shift(@tab);
  next if($day<59);
  if( $day == 59){
    # print $_, "\n";
    #comme les id sont attribues par ordre croissant
    #on note le plus grand id au jour 59 et on
    #ne prendra en compte que les ids superieures a  ce maximum
    my @ids = ();
    # on construit la liste des id present
    foreach( @tab) {
      ($id,$load) = split(':',$_);
      push(@ids,$id);
    }
    # on memorise le plus grand
    $maxid = $ids[0];
    $_ > $maxid and $maxid = $_ for @ids;
    print "maxid = ", $maxid, "\n";
  }
  elsif($day>59){
    foreach(@tab) {
      ($id,$load) = split(':',$_);
      if($id > $maxid){
	push(@{$nodes{$id}},$load);
      }	
    }
  }
}
# creer les listes de sommes et de nombre d occurences
my @sums = ();
my @sizes = ();
foreach my $key ( keys(%nodes)) 
{
  for( my $i = 0;$i < scalar(@{$nodes{$key}});$i++) {
    if(defined($sums[$i])){
      $sums[$i]+=$nodes{$key}[$i];
      $sizes[$i]++;
    }
    else{
      $sums[$i]=$nodes{$key}[$i];
      $sizes[$i] = 1;
    }

  }
}
# calcul moyennes
open (my $fout,">",$dir."/evolutionloads.txt");
for( my $i = 0; $i < scalar(@sums);++$i) {
  print $fout $i,"\t", $sums[$i]/$sizes[$i],"\n";
}
