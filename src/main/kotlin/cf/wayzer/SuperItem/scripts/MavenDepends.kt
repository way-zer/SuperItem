package cf.wayzer.SuperItem.scripts

import cf.wayzer.libraryManager.Repository

@Target(AnnotationTarget.FILE)
@Retention(AnnotationRetention.SOURCE)
@Repeatable
annotation class MavenDepends(
        val name: String,
        val repo: String = Repository.DEFAULT
)
