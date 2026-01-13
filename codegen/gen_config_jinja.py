from YukkuriC.minecraft.codegen.jinja import *

load_data_yaml('config.yaml')
batch_gen(
    load_env(__file__),
    {
        'forge': 'src/main/java/io/yukkuric/mnaop/MNAOPConfig.java',
    },
)
