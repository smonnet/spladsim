#include <stdio.h>

#include "spladsimc.h"

#define MAILBOX_NAME_SIZE 10

XBT_LOG_NEW_DEFAULT_CATEGORY(msg_SpladSimGrid,
		"Messages specific for this simulation");
int peer_function(int argc, char *argv[]);

s_global_data_t gdata;

replica_lease build_replica_lease(spladid id) {
	replica_lease l = xbt_new0(s_replica_lease_t, 1);
	l->entries = (replica_entry) xbt_new0(s_replica_entry_t, gdata.replicarate);
	l->blockid = id;
	return l;
}

replica_lease clone_replica_lease(replica_lease l) {
	replica_lease newlease = build_replica_lease(l->blockid);
	newlease->rootid = l->rootid;
	newlease->tag = l->tag;
	memcpy(newlease->entries, l->entries,
			sizeof(s_replica_entry_t) * gdata.replicarate);
	return newlease;
}
void free_replica_lease(void * pl) {
	replica_lease l = (replica_lease) pl;
	free(l->entries);
	free(l);
}

void free_local_data(local_data_t data) {
	xbt_dict_free(&(data->leases));
	xbt_dict_free(&(data->root));
	xbt_dynar_free(&(data->storage));
	xbt_dynar_free(&data->leafsetByloads);
	free(data);
}

void free_local_data_pvoid(void * data){
	local_data_t local = (local_data_t)data;
	free_local_data(local);
}
local_data_t build_local_data(spladid id) {
	local_data_t l = xbt_new0(s_local_data_t, 1);
	l->storage = xbt_dynar_new(sizeof(bloc), xbt_free_ref);
	l->leases = xbt_dict_new_homogeneous(free_replica_lease);
	l->root = xbt_dict_new_homogeneous(free_replica_lease);
	l->leafsetByloads = xbt_dynar_new(sizeof(load), xbt_free_ref);
	l->id = id;
	return l;
}

bloc clonebloc(bloc b) {
	bloc newbloc = xbt_new0(s_block_t, 1);
	memcpy(newbloc, b, sizeof(s_block_t));
	return newbloc;
}
//tableau de strategy
strategyf tabstrategyf[3];

int churn_generator_function(int argc, char *argv[]) {

	//double t = -7 * 100000 * log(1.0 - RngStream_RandU01(gdata.stream)) / 3;
	//choose a process to kill
	//choose a new id for the new process
	// kill the process
	//clean the data associated to the process
	// add an entry in the dictionnary
	//create the new process
	return 0;
}

int observer_function(int argc, char *argv[]) {

	return 0;
}

int compare_int(int *a, int *b) {
	int ia = *a;
	int ib = *b;
	return (ia < ib) ? -1 : (ia > ib);
}

// le tableau mailboxes doit etre trie
int determineClosestNodeIndex(spladid id) {

	int current_index = 0;
	int previous_index = 0;

	//spladid previous;
	//spladid next;

	while ((current_index < gdata.nhosts)
			&& (xbt_dynar_get_as(gdata.mailboxes,current_index,spladid) < id)) {
		previous_index = current_index;
		current_index++;
	}
	if (current_index == 0) {
		previous_index = gdata.nhosts - 1;
	}
	if (current_index == gdata.nhosts) {
		current_index = 0;
	}
	return (distance(id,
			xbt_dynar_get_as(gdata.mailboxes, current_index, spladid))
			< distance(id,
					xbt_dynar_get_as(gdata.mailboxes, previous_index, spladid))) ?
			current_index : previous_index;
}

void findcandidateRand(replica_lease l, int index, int rootIndex) {
	int found = 0;
	int candidate;
	int tabsize;
	spladid candidateid;
	tabsize = xbt_dynar_length(gdata.mailboxes);
	while (!found) {
		int i;

		// tirer un nombre entre 0 et 2*selectionRange+1
		candidate = RngStream_RandInt(gdata.stream, 0,
				2 * gdata.selectionrange);
		//soustraire selectionRange on obtient un index relatif a l'index root, ajouter l'index du root
		candidate = candidate - gdata.selectionrange + rootIndex;
		//si c'est plus grand ou  egal a la taille du tableau enlever cette taille
		if (candidate >= tabsize)
			candidate = candidate - tabsize;
		//si c'est negatif ajouter la taille
		else if (candidate < 0)
			candidate = candidate + tabsize;
		//candidate est l'index voulu
		candidateid = xbt_dynar_get_as(gdata.mailboxes, candidate, spladid);
		found = 1;
		for (i = 0; i < gdata.replicarate; i++) {
			if (i == index)
				continue;

			if (l->entries[i].id == candidateid) {
				found = 0;
				break;
			}
		}
	}
	//candidat trouve
	//replica_entry r = l->entries, index);
	l->entries[index].hasbloc = 0;
	l->entries[index].id = candidateid;
}

void findcandidatePofChoice(replica_lease l, int index, int rootIndex) {
	// determiner le noeuds dans selectionrange qui ne sont pas deja replicas
	// remplir un dictionnaire (id, charge) trier selon la charge des noeuds
	// tirer deux nombre au hasard
	//prendre le moins charge des deux (index le plus petit)
	// mettre a jour la charge dans la copie

}
void printstorage(spladid id) {
	local_data_t local = (local_data_t) xbt_dict_get_or_null_ext(
			gdata.process_data_dict, (char *) (&(id)), sizeof(spladid));
	if (local == NULL) {
		XBT_ERROR("pas de stockage associe a %lu", id);
	} else {
		unsigned int j;
		bloc b;
		printf("blocs stockes sur %lu :", id);
		xbt_dynar_foreach(local->storage,j,b)
		{
			printf("%lu", b->blockid);
		}
		printf("\n");
	}

}
//Random strategy
void fillstorageRANDOM() {
	int i, closestIndex, j;
	spladid id;
	for (i = 0; i < gdata.blocs; i++) {
		replica_lease rl;
		bloc bl = xbt_new0(s_block_t, 1);
		id = randUINT64(gdata.stream);
		bl->blockid = id;
		closestIndex = determineClosestNodeIndex(id);
		//printf("closest index: %d",closestIndex);
		rl = build_replica_lease(id);
		rl->tag = KEEP_STORING;
		rl->rootid = xbt_dynar_get_as(gdata.mailboxes, closestIndex, spladid);
		//printf( "rootid is %020lu for id %020lu\n",rl->rootid,id);
		for (j = 0; j < gdata.replicarate; j++) {
			findcandidateRand(rl, j, closestIndex);
			// on rempli le stockage
			rl->entries[j].hasbloc = 1;
		}
//on remplit le stockage de chaque replica avec un clone de lease
		for (j = 0; j < gdata.replicarate; j++) {
			replica_lease l = clone_replica_lease(rl);
			local_data_t local = (local_data_t) xbt_dict_get_ext(
					gdata.process_data_dict, (char *) (&(l->entries[j].id)),
					sizeof(spladid));
			xbt_dynar_t storage = local->storage;
			bloc b = clonebloc(bl);
			xbt_dynar_push(storage, &b);
			xbt_dict_set_ext(local->leases, (char *) &l->blockid,
					sizeof(spladid), l, NULL);
		}
		//mettre la lease dans la liste du root
		local_data_t local = xbt_dict_get_ext(gdata.process_data,
				(char *) (&(rl->rootid)), sizeof(spladid));
		xbt_dict_set_ext(local->root, (char *) &rl->blockid, sizeof(spladid),
				rl, NULL);
		// il reste une copie du bloc, on libere la memoire
		free(bl);
	}
}
//Power of choice strategy
void fillstoragePOC() {

}
//LESSCHARGED strategy
void fillstorageLESS() {

}
// initialise la charge des noeuds
void fillstorage() {
	switch (gdata.strat) {
	case RANDOM:
		fillstorageRANDOM();
		break;
	case POWEROFCHOICE:
		fillstoragePOC();
		break;
	case LESSCHARGED:
		fillstorageLESS();
		break;
	default:
		XBT_INFO("unknown strategy exiting");
		exit(1);
	}

}

//insere et trie le tableau
void insertinstorage(xbt_dynar_t storage, spladid id) {
	xbt_dynar_push_as(storage, spladid, id);
	xbt_dynar_sort(storage, compareUint64);
}

//recherche dichotomique dans tableau trie
//le tableau doit contenir des blocs
int dichosearch(xbt_dynar_t tab, spladid val) {
	spladid midval;
	size_t size = xbt_dynar_length(tab);
	if (size == 0)
		return -1;
	size_t hi = size - 1;
	size_t lo = 0;
	size_t mid = (hi + lo) / 2;
	while (mid < size && lo <= hi) {
		xbt_assert(mid < size);
		midval = ((bloc) xbt_dynar_get_ptr(tab, mid))->blockid;
		if (val < midval)
			hi = mid - 1;
		else if (val > midval)
			lo = mid + 1;
		else
			return mid;
		mid = (hi + lo) / 2;
	}
	return -1;
}

// fill nodes with blocks according to strategy
int initialisation_function(int argc, char *argv[]) {
	msg_host_t host;
	unsigned int compt = 0;
	//debug
	spladid cpt;
	unsigned int cursor;
	//tableau des hotes
	xbt_dynar_t host_dynar = MSG_hosts_as_dynar();
	xbt_dynar_foreach(host_dynar,compt,host)
	{
		MSG_process_create("peer_function", peer_function, NULL, host);
	}
	MSG_process_sleep(1);
	//debug lister les mailboxes
	xbt_dynar_foreach(gdata.mailboxes,cursor,cpt)
	{
		XBT_INFO("%u entree vaut %lu", cursor, cpt);
	}
	// les peers se sont initialises
	//repartir les donnes
	int size = xbt_dynar_length(host_dynar);
	gdata.nhosts = size;

	// fill gdata
	fillstorage();
	//launch observer process
	// create  peer processes
	//create churn generator process
	xbt_dynar_free(&host_dynar);
	return 0;
}

//processus en charge de recevoir les messages entrant
int peer_receiver(int argc, char* argv[]) {
	return 0;
}

//processus en charge d'envoyer les blocs
int peer_sender(int argc, char* argv[]) {
	return 0;
}

//process en charge de recevoir les nouvelles leases
int peer_control_receiver(int argc, char* argv[]) {
	return 0;
}
spladid rootOf(spladid blockid, spladid hint, spladid myid) {
	spladid disthint, nextdistance, currentdistance;
	local_data_t local;
	int nextIndex, currentIndex;
	if (dichosearch(gdata.mailboxes, hint) == -1) {
		//le root est mort
		hint = myid;
	}
	local = (local_data_t) xbt_dict_get_ext(gdata.process_data,
			(char *) (&hint), sizeof(spladid));
	disthint = distance(hint, blockid);
	nextdistance = disthint;
	//recherche a droite
	do {
		currentdistance = nextdistance;
		currentIndex = nextIndex;
		nextIndex = (nextIndex + 1) % xbt_dynar_length(gdata.mailboxes);
		nextdistance = distance(blockid,
				xbt_dynar_get_as(gdata.mailboxes, nextIndex, spladid));
	} while (nextdistance < currentdistance);
	//faut il chercher a gauche ?
	if (local->index == currentIndex) {
		nextdistance = disthint;
		nextIndex = currentIndex;
		do {
			currentdistance = nextdistance;
			currentIndex = nextIndex;
			nextIndex =
					(nextIndex == 0) ?
							xbt_dynar_length(gdata.mailboxes) - 1 :
							nextIndex - 1;
			nextdistance = distance(blockid,
					xbt_dynar_get_as(gdata.mailboxes, nextIndex, spladid));
		} while (nextdistance < currentdistance);
	}
	return xbt_dynar_get_as(gdata.mailboxes, nextIndex, spladid);
}
void doReplicaJob(xbt_dict_t updateMessages, spladid myid) {
	unsigned int cursor;
	bloc bl;
	replica_lease current;
	spladid root;
	xbt_dynar_t removelist = xbt_dynar_new(sizeof(replica_lease),
			free_replica_lease);
	local_data_t local = (local_data_t) xbt_dict_get_ext(gdata.process_data,
			(char *) (&myid), sizeof(spladid));
	xbt_dynar_t storage = local->storage;
	xbt_dynar_t orders;
	replica_lease dolly;

	xbt_dynar_foreach(storage,cursor,bl)
	{
		current = (replica_lease) xbt_dict_get_or_null_ext(local->leases,
				(char *)&(bl->blockid),sizeof(spladid));
		if (current == NULL) {
			xbt_dynar_push(removelist, current);
			continue;
		}
		root = rootOf(current->blockid, current->rootid, myid);
		if (current->rootid != root) {
			orders = xbt_dict_get_or_null_ext(updateMessages,(char *) &root,
					sizeof(spladid));
			if (orders == NULL) {
				orders = xbt_dynar_new(sizeof(replica_lease),
						free_replica_lease);
				xbt_dict_set_ext(updateMessages, (char *) &root,
						sizeof(spladid), orders, NULL);
			}

			if (root != myid) {
				if (current->rootid != myid) {
					dolly = clone_replica_lease(current);
					dolly->tag = NEW_ROOT;
					xbt_dynar_push(orders, dolly);
				}
			} else {
				replica_lease l = (replica_lease) xbt_dict_get_or_null_ext(
						local->root, (char *) &(current->blockid),
						sizeof(spladid));
				if (l == NULL) {
					dolly = clone_replica_lease(current);
					dolly->tag = NEW_ROOT;
					xbt_dict_set_ext(local->root, (char *) &(current->blockid),
							sizeof(spladid), dolly, NULL);
				}
			}
		}
	}
	xbt_dynar_free(&removelist);
}

void doRootJob(xbt_dict_t updateMessages, spladid myid) {

}
void redownloadMissing(spladid myid) {

}

void performReplicaSetUpdate(spladid myid) {
	//hashmap des messages  d'update
	xbt_dict_t updateMessages = xbt_dict_new_homogeneous(xbt_dynar_free_voidp);
	doReplicaJob(updateMessages, myid);
	doRootJob(updateMessages, myid);
	redownloadMissing(myid);
}
void updateIndexes() {
	unsigned int cursor;
	spladid id;
	xbt_dynar_foreach(gdata.mailboxes,cursor,id)
	{
		local_data_t local = (local_data_t) xbt_dict_get_ext(gdata.process_data,
				(char *)&id, sizeof(spladid));
		local->index = cursor;
	}
}
int peer_function(int argc, char *argv[]) {
	//on obtient un id
	spladid id = randUINT64(gdata.stream);
	//on cree l'entree dans le dictionnaire
	xbt_dynar_push_as(gdata.mailboxes, spladid, id);
	xbt_dynar_sort(gdata.mailboxes, compareUint64);

	//initialisation du stockage
	//dictionnaire global pour faciliter le travail de l'observer
	//xbt_dynar_t storage = xbt_dynar_new(sizeof(bloc), NULL);
	local_data_t local = build_local_data(id);
	xbt_dict_set_ext(gdata.process_data_dict, (char *) (&id), sizeof(spladid),
			local, NULL);
	updateIndexes();
	//debug
//	xbt_dynar_t test = xbt_dict_get_ext(gdata.storage_dict,(char *)(&id),sizeof(spladid));

	MSG_process_set_data(MSG_process_self(), &id);
	MSG_process_sleep(RngStream_RandInt(gdata.stream, 1, 10));
	xbt_queue_t queue = xbt_queue_new(0, sizeof(s_insert_message_t));
	// on lance les senders et receivers
	MSG_process_create("peer_sender", peer_sender, queue, MSG_host_self());
	MSG_process_create("peer_control_reciever", peer_control_receiver, &id,
			MSG_host_self());
	MSG_process_create("peer_reciever", peer_receiver, &id, MSG_host_self());

	// maintenance
	while (1) {
		performReplicaSetUpdate(id);
	}
	return 0;
}

int main(int argc, char *argv[]) {
	char * platform;
	enum strategy strat = RANDOM;
	int nblocs = 100;
	int bsize = 1000; // kilo_octets
	int opt;
	int replicarate = 3;
	char *s;
	MSG_init(&argc, argv);

	while ((opt = getopt(argc, argv, "p:n:s:r:")) != -1) {
		switch (opt) {
		case 'n':
			nblocs = atoi(optarg);
			break;
		case 'p':
			s = bprintf("%s", optarg);
			if (!strncmp(s, "LESSCHARGED", 11)) {
				strat = LESSCHARGED;
			} else if (!strncmp(s, "RANDOM", 6)) {
				strat = RANDOM;
			} else if (!strncmp(s, "POWEROFCHOICE", 12)) {
				strat = POWEROFCHOICE;
			} else {
				printf("** ERROR unknown strategy **\n");
				exit(1);
			}
			free(s);
			break;
		case 's':
			bsize = atoi(optarg);
			break;
		case 'r':
			replicarate = atoi(optarg);
		}
	}

	/* check usage error and initialize with defaults */
	if (argc - optind != 1) {
		printf("** ERROR **\n");
		exit(1);
	}
	platform = argv[optind];
	msg_error_t res = MSG_OK;

	/* Simulation setting */
	MSG_create_environment(platform);

	/* Application deployment */
	MSG_function_register("peer_function", peer_function);
	MSG_function_register("initialisation_function", initialisation_function);
	MSG_function_register("observer_function", observer_function);
	MSG_function_register("peer_receiver", peer_receiver);
	MSG_function_register("peer_sender", peer_sender);
	MSG_function_register("peer_receiver", peer_receiver);
	MSG_function_register("peer_control_receiver", peer_control_receiver);
	//initialisation des donnees globales

	gdata.stream = RngStream_CreateStream("Rand");
	gdata.mailboxes = xbt_dynar_new(sizeof(spladid), NULL);
	gdata.process_data_dict = xbt_dict_new_homogeneous(xbt_dynar_free_voidp);
	gdata.strat = strat;
	gdata.blocssize = bsize;
	gdata.blocs = nblocs;
	gdata.replicarate = replicarate;
	gdata.process_data = xbt_dict_new_homogeneous(free_local_data_pvoid);
	//TODO parametrer
	gdata.selectionrange = 2;
	//initialiser la strategie

	//creation du process initilisateur
	msg_host_t host;
	xbt_dynar_t host_dynar = MSG_hosts_as_dynar();

	//unsigned int compt = 0;
	//on demarre le controller sur le premier hote
	host = xbt_dynar_getfirst_as(host_dynar, msg_host_t);
	MSG_process_create("controller", initialisation_function, NULL, host);

	xbt_dynar_free(&host_dynar);
	res = MSG_main();
	XBT_INFO("Simulation time %g", MSG_get_clock());

	if (res == MSG_OK) {
		return 0;
	} else {
		return 1;
	}
} /* end_of_main */

