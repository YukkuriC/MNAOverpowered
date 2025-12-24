package io.yukkuric.mnaop;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = MNAOPMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class MNAOPConfig {
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    // entries
    {%- for grp,lines in group_val(data,'type') %}
    public static ForgeConfigSpec.{{grp.capitalize()}}Value{% for line in lines %}
            Cfg_{{line.name}}{% if loop.last %};{% else %},{% endif %}
        {%- endfor %}
    {%- endfor %}

    static {
    {%- for grp,lines in group_val(data,'category') %}
        {%- if grp == 'misc' %}
        {%- for line in lines %}
        Cfg_{{line.name}} = BUILDER.comment("{{line.descrip}}").define{{define_sub(line)}}("{{line.name}}", {{line.default}});
        {%- endfor %}
        {%- else %}
        if (MNAOPMod.ConfigGroupActive("{{grp}}")) {
            BUILDER.push("{{grp}}");
            {%- for line in lines %}
            Cfg_{{line.name}} = BUILDER.comment("{{line.descrip}}").define{{define_sub(line)}}("{{line.name}}", {% if line.type == 'enum' %}MNAOPEnums.{{line.enumType}}.{% endif %}{{line.default}});
            {%- endfor %}
            BUILDER.pop();
        }
        {%- endif %}
    {%- endfor %}
    }

    static final ForgeConfigSpec SPEC = BUILDER.build();

    // interfaces
    {%- for line in data %}
    public static {% if line.type == 'enum' %}MNAOPEnums.{{line.enumType}}{% else %}{{line.type}}{% endif %} {{line.name}}() {
        return {% if line.type == 'enum' %}(MNAOPEnums.{{line.enumType}}) {% endif %}Cfg_{{line.name}}.get();
    }
    {%- endfor %}
}
