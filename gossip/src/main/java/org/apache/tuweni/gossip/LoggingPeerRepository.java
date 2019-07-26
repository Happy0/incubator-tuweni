/*
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license agreements. See the NOTICE
 * file distributed with this work for additional information regarding copyright ownership. The ASF licenses this file
 * to You under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.apache.tuweni.gossip;

import org.apache.tuweni.plumtree.Peer;
import org.apache.tuweni.plumtree.PeerRepository;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

final class LoggingPeerRepository implements PeerRepository {

  private final Set<Peer> eagerPushPeers = ConcurrentHashMap.newKeySet();
  private final Set<Peer> lazyPushPeers = ConcurrentHashMap.newKeySet();
  private final PrintStream logger;

  public LoggingPeerRepository(PrintStream logger) {
    this.logger = logger;
  }

  @Override
  public void addEager(Peer peer) {
    eagerPushPeers.add(peer);
  }

  @Override
  public List<Peer> peers() {
    List<Peer> peers = new ArrayList<>(eagerPushPeers);
    peers.addAll(lazyPushPeers);
    return peers;
  }

  @Override
  public Collection<Peer> lazyPushPeers() {
    return lazyPushPeers;
  }

  @Override
  public Collection<Peer> eagerPushPeers() {
    return eagerPushPeers;
  }

  @Override
  public void removePeer(Peer peer) {
    logger.println("Removing peer " + peer);
    lazyPushPeers.remove(peer);
    eagerPushPeers.remove(peer);
  }

  @Override
  public boolean moveToLazy(Peer peer) {
    logger.println("Move peer to lazy " + peer);
    eagerPushPeers.remove(peer);
    lazyPushPeers.add(peer);
    return true;
  }

  @Override
  public void moveToEager(Peer peer) {
    logger.println("Move peer to eager " + peer);
    lazyPushPeers.remove(peer);
    eagerPushPeers.add(peer);
  }

  @Override
  public void considerNewPeer(Peer peer) {
    if (!lazyPushPeers.contains(peer)) {
      if (eagerPushPeers.add(peer)) {
        logger.println("Added new peer " + peer);
      }
    }

  }

}
