#!/usr/bin/perl

use File::Find;
use Archive::Tar;

# note, this should be run from the src/ directory, or the first parameter
# should be the path to src/

$target = "./src";
$tar    = Archive::Tar->new();

if( scalar(@ARGV) > 0)
{
    $target = $ARGV[0];
}

print "searching in $target\n";

find({
	'wanted'=>\&wanted,
	'no_chdir'=>1,
	}, $target);
	
$tar->write("binpack.tar.gz", COMPRESS_GZIP);

sub wanted
{
	if( /\.png$/i  )
	{
        $tar->add_files( $_ );
        print "Adding $_\n";
	}
}

