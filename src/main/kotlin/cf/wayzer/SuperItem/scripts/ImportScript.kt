package cf.wayzer.SuperItem.scripts

@Target(AnnotationTarget.FILE)
@Retention(AnnotationRetention.SOURCE)
@Repeatable
annotation class ImportScript (
        val path:String
)
