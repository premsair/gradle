package patches.buildTypes

import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.ui.*

/*
This patch script was generated by TeamCity on settings change in UI.
To apply the patch, change the buildType with id = 'Check_Parallel_7_bucket2'
accordingly, and delete the patch script.
*/
changeBuildType(RelativeId("Check_Parallel_7_bucket2")) {
    vcs {
        remove(AbsoluteId("Gradle_Branches_GradlePersonalBranches"))
        add(AbsoluteId("GradleWithoutDummy"))
    }
}
