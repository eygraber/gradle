/*
 * Copyright 2020 the original author or authors.
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

package org.gradle.internal.watch.vfs.impl

import org.gradle.internal.snapshot.AtomicSnapshotHierarchyReference
import org.gradle.internal.snapshot.CaseSensitivity
import org.gradle.internal.snapshot.SnapshotHierarchy
import org.gradle.internal.vfs.impl.DefaultSnapshotHierarchy
import spock.lang.Specification

class WatchingNotSupportedFileSystemWatchingHandlerTest extends Specification {
    def emptySnapshotHierarchy = DefaultSnapshotHierarchy.empty(CaseSensitivity.CASE_SENSITIVE)
    def nonEmptySnapshotHierarchy = Stub(SnapshotHierarchy) {
        empty() >> emptySnapshotHierarchy
    }
    def root = new AtomicSnapshotHierarchyReference(nonEmptySnapshotHierarchy)
    def watchingNotSupportedHandler = new WatchingNotSupportedFileSystemWatchingHandler(root)

    def "invalidates the virtual file system before and after the build"() {

        when:
        watchingNotSupportedHandler.afterBuildStarted(retentionEnabled)
        then:
        root.get() == emptySnapshotHierarchy

        when:
        root.update { nonEmptySnapshotHierarchy }
        watchingNotSupportedHandler.beforeBuildFinished(retentionEnabled)
        then:
        root.get() == emptySnapshotHierarchy

        where:
        retentionEnabled << [true, false]
    }
}
