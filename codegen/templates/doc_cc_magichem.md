# MagiChem x CC:Tweaked Extra Peripheral Document
{% for file,types in sorted(typesByFile.items()) %}
## {{ wrapFile(file) }}
_Source: [{{ file }}](https://github.com/YukkuriC/MNAOverpowered/tree/main/src/main/java/io/yukkuric/mnaop/magichem/cc/{{ file }})_
{%- for type in sorted(types) %}
### {{ wrapType(type) }}
{%- for rType, func, args in methodsByType[type] %}
- `{{ rType }} {{ func }}({{args}})`
{%- endfor %}
{% endfor %}
{%- endfor %}