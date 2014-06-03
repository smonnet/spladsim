#! /usr/bin/perl
use strict;
use warnings;
use Statistics::Descriptive;
use File::Basename;

my $stat = Statistics::Descriptive::Sparse->new();
while(<>) {
  chomp();
  $stat->add_data($_);
}
print "# mean  standard_deviation min max                     \n"; 
print $stat->mean()." ".$stat->standard_deviation()." ".$stat->min()." ".$stat->max()."\n";
