{{/* vim: set filetype=mustache: */}}
{{/*
Expand the name of the chart.
*/}}
{{- define "catalog.name" -}}
{{- default .Chart.Name .Values.nameOverride | trunc 63 | trimSuffix "-" }}
{{- end }}

{{/*
Create a default fully qualified app name.
We truncate at 63 chars because some Kubernetes name fields are limited to this (by the DNS naming spec).
If release name contains chart name it will be used as a full name.
*/}}
{{- define "catalog.fullname" -}}
{{- if .Values.fullnameOverride }}
{{- .Values.fullnameOverride | trunc 63 | trimSuffix "-" }}
{{- else }}
{{- $name := default .Chart.Name .Values.nameOverride }}
{{- if contains $name .Release.Name }}
{{- .Release.Name | trunc 63 | trimSuffix "-" }}
{{- else }}
{{- printf "%s-%s" .Release.Name $name | trunc 63 | trimSuffix "-" }}
{{- end }}
{{- end }}
{{- end }}

{{/*
Create chart name and version as used by the chart label.
*/}}
{{- define "catalog.chart" -}}
{{- printf "%s-%s" .Chart.Name .Chart.Version | replace "+" "_" | trunc 63 | trimSuffix "-" }}
{{- end }}

{{/*
Common labels
*/}}
{{- define "catalog.labels" -}}
helm.sh/chart: {{ include "catalog.chart" . }}
{{ include "catalog.selectorLabels" . }}
{{- if .Chart.AppVersion }}
app.kubernetes.io/version: {{ .Chart.AppVersion | quote }}
{{- end }}
app.kubernetes.io/managed-by: {{ .Release.Service }}
{{- end }}

{{/*
Selector labels
*/}}
{{- define "catalog.selectorLabels" -}}
app.kubernetes.io/name: {{ include "catalog.name" . }}
app.kubernetes.io/instance: {{ .Release.Name }}
app.kubernetes.io/component: service
app.kuberneres.io/owner: retail-store-sample
{{- end }}

{{/*
Create the name of the service account to use
*/}}
{{- define "catalog.serviceAccountName" -}}
{{- if .Values.serviceAccount.create }}
{{- default (include "catalog.fullname" .) .Values.serviceAccount.name }}
{{- else }}
{{- default "default" .Values.serviceAccount.name }}
{{- end }}
{{- end }}

{{/*
Create the name of the config map to use
*/}}
{{- define "catalog.configMapName" -}}
{{- if .Values.configMap.create }}
{{- default (include "catalog.fullname" .) .Values.configMap.name }}
{{- else }}
{{- default "default" .Values.configMap.name }}
{{- end }}
{{- end }}


{{/* podAnnotations */}}
{{- define "catalog.podAnnotations" -}}
{{- if or .Values.metrics.enabled .Values.podAnnotations }}
{{- $podAnnotations := .Values.podAnnotations}}
{{- $metricsAnnotations := .Values.metrics.podAnnotations}}
{{- $allAnnotations := merge $podAnnotations $metricsAnnotations }}
{{- toYaml $allAnnotations }}
{{- end }}
{{- end -}}

{{- define "catalog.mysql.fullname" -}}
{{- include "catalog.fullname" . }}-mysql
{{- end -}}

{{/*
Common labels for mysql
*/}}
{{- define "catalog.mysql.labels" -}}
helm.sh/chart: {{ include "catalog.chart" . }}
{{ include "catalog.mysql.selectorLabels" . }}
{{- if .Chart.AppVersion }}
app.kubernetes.io/version: {{ .Chart.AppVersion | quote }}
{{- end }}
app.kubernetes.io/managed-by: {{ .Release.Service }}
{{- end }}

{{/*
Selector labels for mysql
*/}}
{{- define "catalog.mysql.selectorLabels" -}}
app.kubernetes.io/name: {{ include "catalog.fullname" . }}
app.kubernetes.io/instance: {{ .Release.Name }}
app.kubernetes.io/component: mysql
{{- end }}

{{- define "getOrGeneratePass" }}
{{- $len := (default 16 .Length) | int -}}
{{- $obj := (lookup "v1" .Kind .Namespace .Name).data -}}
{{- if $obj }}
{{- index $obj .Key -}}
{{- else if (eq (lower .Kind) "secret") -}}
{{- randAlphaNum $len | b64enc -}}
{{- else -}}
{{- randAlphaNum $len -}}
{{- end -}}
{{- end }}

{{- define "catalog.mysql.password" -}}
{{- if not (empty .Values.mysql.secret.password) -}}
  {{- .Values.mysql.secret.password | b64enc -}}
{{- else -}}
    {{- include "getOrGeneratePass" (dict "Namespace" .Release.Namespace "Kind" "Secret" "Name" .Values.mysql.secret.name "Key" "password") -}}
{{- end -}}
{{- end -}}

{{- define "catalog.mysql.reader.password" -}}
{{- if not (empty .Values.mysql.reader.secret.password) -}}
  {{- .Values.mysql.reader.secret.password | b64enc -}}
{{- else -}}
    {{- include "getOrGeneratePass" (dict "Namespace" .Release.Namespace "Kind" "Secret" "Name" .Values.mysql.reader.secret.name "Key" "password") -}}
{{- end -}}
{{- end -}}

{{- define "catalog.mysql.endpoint" -}}
{{- if not (empty .Values.mysql.endpoint) -}}
    {{- .Values.mysql.endpoint -}}
{{- else -}}
{{ include "catalog.mysql.fullname" . }}:{{ .Values.mysql.service.port }}
{{- end -}}
{{- end -}}

{{- define "catalog.mysql.reader.endpoint" -}}
{{- if not (empty .Values.mysql.reader.endpoint) -}}
    {{- .Values.mysql.reader.endpoint -}}
{{- else -}}
    {{- include "catalog.mysql.endpoint" . -}}
{{- end -}}
{{- end -}}