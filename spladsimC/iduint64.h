/*
 * iduint64.h
 *
 *  Created on: Jun 14, 2014
 *      Author: veronique
 */

#ifndef IDUINT64_H_
#define IDUINT64_H_

#include "stdint.h"
#include "xbt/RngStream.h"

typedef uint64_t spladid;
spladid randUINT64(RngStream stream);
int compareUint64(const void * a, const void *b);
spladid distance(spladid a, spladid b);
#endif /* IDUINT64_H_ */
