# Fondo Compared With Freenet

## Authorship

From [Wikipedia's entry on Freenet][WF]:

> Encryption of data and relaying of requests makes it difficult to determine
> who inserted content into Freenet, who requested that content, or where the
> content was stored. This protects the anonymity of participants, and also
> makes it very difficult to censor specific content. Content is stored
> encrypted, making it difficult for even the operator of a node to determine
> what is stored on that node.

Fondo does not guarantee anonymity of participants nor does it prevent it. Fondo
is a distributed, layered system. Each Fondo Zone can enforce its own
authentication rules. A Zone may choose to allow anonymous access or
authenticated access, or some combination.

## Data Loss

From [Wikipedia's entry on Freenet][WF]:

> The key disadvantage of the storage method [in Freenet] is that no one node is
> responsible for any chunk of data. If a piece of data is not retrieved for
> some time and a node keeps getting new data, it will drop the old data
> sometime when its allocated disk space is fully used. In this way Freenet
> tends to 'forget' data which is not retrieved regularly (see also Effect).

Fondo can also lose data, but for different reasons.

A core principle of Fondo is data immutability, so data deletion is not
recommended in most cases. Fondo does not directly store the underlying data, so
if a content publisher wishes to delete the underlying data (referred to by a
URI), Fondo cannot stop them. However, before that happens, other Fondo Nodes
may have cloned the data.

Even though data deletion is against the spirit of immutability, it is allowed,
because the system designers can anticipate situations where it might be
reasonable.

[WF]: https://en.wikipedia.org/wiki/Freenet
