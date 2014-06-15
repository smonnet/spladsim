/*
 * iduint64.c
 *
 *  Created on: Jun 14, 2014
 *      Author: veronique
 */

#include "iduint64.h"

#include <limits.h> // INT_MAX
// return a non 0 uint64
spladid randUINT64(RngStream stream) {

	spladid res = 0;
	while (res == 0) {
		res = (spladid) ((ULLONG_MAX + 1.0) * RngStream_RandU01(stream));
	}
	return res;
}

int compareUint64(const void * a, const void *b) {
	spladid ua;
	spladid ub;
	ua = *((spladid *)a);
	ub = *((spladid *)b);
	return (ua < ub) ? -1 : (ua > ub);
}

//pas d'id a zero
spladid distance(spladid a, spladid b) {
	if (a == b)
		return 0;

	int c = (a < b) ? -1 : 1;
	spladid fwdist = (a - b) * c;
	spladid bwdist = (c > 0) ? (ULLONG_MAX - a) + b : (ULLONG_MAX - b) + a;
	return (fwdist < bwdist) ? fwdist : bwdist;

}
