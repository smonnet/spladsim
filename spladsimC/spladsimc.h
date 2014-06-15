/*
 * spladsimc.h
 *
 *  Created on: Jun 12, 2014
 *      Author: veronique
 */

#ifndef SPLADSIMC_H_
#define SPLADSIMC_H_

#include "msg/msg.h"
#include "xbt/sysdep.h"
#include "xbt/RngStream.h"
#include "iduint64.h"
/* Create a log channel to have nice outputs. */
#include "xbt/log.h"
#include "xbt/asserts.h"
#include <getopt.h>
#include "math.h"

enum strategy {
	LESSCHARGED, POWEROFCHOICE, RANDOM
};

typedef enum lease_tag {
	NEW_ROOT,KEEP_STORING
}lease_tag_t;
//application structures
//global data structure
typedef struct s_global_data {
	xbt_dynar_t mailboxes;
	xbt_dict_t process_data_dict;
	RngStream stream; // pour chaque process la liste des blocs stockes
	enum strategy strat;
	int blocs;
	int blocssize;
	int nhosts;
	int replicarate;
	int selectionrange;
	xbt_dict_t process_data;
} s_global_data_t, *global_data;


//host local data
typedef struct s_local_data {
	xbt_dict_t leases;
	xbt_dict_t root; // hash des leases dont on est root
	xbt_dynar_t storage; //tableaux de blocs
	spladid id; // id du noeud
	size_t index; //index dans le tableau des ids
	xbt_dynar_t leafsetByloads;// doit etre rempli au debut du cycle de maintenance
} s_local_data_t, *local_data_t;


void free_local_data(local_data_t data);
typedef struct s_block {
	spladid blockid;
	size_t size;
} s_block_t, *bloc;

typedef struct s_replica_entry {
	spladid id;
	int hasbloc;
} s_replica_entry_t, *replica_entry;

typedef struct s_replica_lease {
	spladid rootid;
	spladid blockid;
	replica_entry entries;
	lease_tag_t tag;
} s_replica_lease_t, *replica_lease;


typedef struct s_load{
	spladid id;
	int load;
}s_load_t,load;

//strategies de placement
typedef int (*strategyf)(replica_lease l, int index, int rootIndex);

typedef struct s_insert_message{
	spladid from;
	spladid to;
	spladid contentID;
	unsigned int content_size;

}s_insert_message_t,*insert_message_t;
#endif /* SPLADSIMC_H_ */
