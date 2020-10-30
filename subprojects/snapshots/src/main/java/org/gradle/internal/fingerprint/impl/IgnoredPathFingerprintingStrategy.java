/*
 * Copyright 2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.gradle.internal.fingerprint.impl;

import com.google.common.collect.ImmutableMap;
import org.gradle.internal.fingerprint.FileSystemLocationFingerprint;
import org.gradle.internal.fingerprint.FingerprintHashingStrategy;
import org.gradle.internal.snapshot.CompleteFileSystemLocationSnapshot;
import org.gradle.internal.snapshot.CompleteFileSystemLocationSnapshot.FileSystemLocationSnapshotVisitor;
import org.gradle.internal.snapshot.FileSystemSnapshot;
import org.gradle.internal.snapshot.FileSystemSnapshotHierarchyVisitor;
import org.gradle.internal.snapshot.MissingFileSnapshot;
import org.gradle.internal.snapshot.RegularFileSnapshot;

import java.util.HashSet;
import java.util.Map;

/**
 * Fingerprint files ignoring the path.
 *
 * Ignores directories.
 */
public class IgnoredPathFingerprintingStrategy extends AbstractFingerprintingStrategy {

    public static final IgnoredPathFingerprintingStrategy INSTANCE = new IgnoredPathFingerprintingStrategy();
    public static final String IDENTIFIER = "IGNORED_PATH";
    public static final String IGNORED_PATH = "";

    private IgnoredPathFingerprintingStrategy() {
        super(IDENTIFIER);
    }

    @Override
    public String normalizePath(CompleteFileSystemLocationSnapshot snapshot) {
        return IGNORED_PATH;
    }

    @Override
    public Map<String, FileSystemLocationFingerprint> collectFingerprints(Iterable<? extends FileSystemSnapshot> roots) {
        ImmutableMap.Builder<String, FileSystemLocationFingerprint> builder = ImmutableMap.builder();
        HashSet<String> processedEntries = new HashSet<>();
        for (FileSystemSnapshot root : roots) {
            root.accept(snapshot -> {
                snapshot.accept(new FileSystemLocationSnapshotVisitor() {
                    @Override
                    public void visitRegularFile(RegularFileSnapshot fileSnapshot) {
                        visitNonFileEntry(snapshot);
                    }

                    @Override
                    public void visitMissing(MissingFileSnapshot missingSnapshot) {
                        visitNonFileEntry(snapshot);
                    }

                    private void visitNonFileEntry(CompleteFileSystemLocationSnapshot snapshot) {
                        String absolutePath = snapshot.getAbsolutePath();
                        if (processedEntries.add(absolutePath)) {
                            builder.put(absolutePath, IgnoredPathFileSystemLocationFingerprint.create(snapshot.getType(), snapshot.getHash()));
                        }
                    }
                });
                return FileSystemSnapshotHierarchyVisitor.SnapshotVisitResult.CONTINUE;
            });
        }
        return builder.build();
    }

    @Override
    public FingerprintHashingStrategy getHashingStrategy() {
        return FingerprintHashingStrategy.SORT;
    }
}
