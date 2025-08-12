
sealed class Platform {
    data object Android : Platform()
    data object Web : Platform()
}

expect val platform: Platform


