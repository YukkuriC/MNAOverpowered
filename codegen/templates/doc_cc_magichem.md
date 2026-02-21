# MagiChem x CC:Tweaked Extra Peripheral Document
{% for file,types in sorted(typesByFile.items()) %}
## {{ wrapFile(file) }}
{%- for type in sorted(types) %}
### {{ wrapType(type) }}
{%- for rType, func, args in methodsByType[type] %}
- `{{ rType }} {{ func }}({{args}})`
{%- endfor %}
{% endfor %}
{%- endfor %}