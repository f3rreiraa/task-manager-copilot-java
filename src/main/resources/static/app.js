const api='/api/tasks'; const form=document.querySelector('#task-form'); const list=document.querySelector('#tasks'); const message=document.querySelector('#message'); const loading=document.querySelector('#loading');
const fields={id:document.querySelector('#task-id'),title:document.querySelector('#title'),description:document.querySelector('#description'),status:document.querySelector('#status'),priority:document.querySelector('#priority')};
function notify(text,type='success'){message.textContent=text;message.className=type;}
function reset(){form.reset();fields.id.value='';document.querySelector('#form-title').textContent='Nova tarefa';document.querySelector('#cancel').hidden=true;}
async function request(url,options={}){const response=await fetch(url,{headers:{'Content-Type':'application/json'},...options});if(response.status===204)return null;const data=await response.json().catch(()=>({}));if(!response.ok){const details=data.errors?Object.values(data.errors).join(' | '):'';throw new Error((data.message||'Falha na operação')+(details?': '+details:''));}return data;}
async function load(){loading.hidden=false;list.innerHTML='';try{const tasks=await request(api+buildQuery());if(tasks.length===0){list.innerHTML='<li class="empty">Nenhuma tarefa encontrada.</li>';}tasks.forEach(render);}catch(e){notify(e.message,'error');}finally{loading.hidden=true;}}
function render(task){const li=document.createElement('li');li.className='task';li.innerHTML=`<div class="row"><div><h3></h3><p></p><span class="badge status"></span> <span class="badge priority"></span></div><div class="task-actions"><button class="edit secondary">Editar</button><button class="delete">Excluir</button></div></div>`;li.querySelector('h3').textContent=task.title;li.querySelector('p').textContent=task.description||'Sem descrição';li.querySelector('.badge.status').textContent=task.status;li.querySelector('.badge.priority').textContent=task.priority;li.querySelector('.badge.priority').classList.add(task.priority.toLowerCase());li.querySelector('.edit').onclick=()=>{fields.id.value=task.id;fields.title.value=task.title;fields.description.value=task.description||'';fields.status.value=task.status;fields.priority.value=task.priority;document.querySelector('#form-title').textContent='Editar tarefa';document.querySelector('#cancel').hidden=false;scrollTo({top:0,behavior:'smooth'});};li.querySelector('.delete').onclick=async()=>{if(confirm(`Excluir "${task.title}"?`)){try{await request(`${api}/${task.id}`,{method:'DELETE'});notify('Tarefa excluída.');load();}catch(e){notify(e.message,'error');}}};list.appendChild(li);}
form.addEventListener('submit',async e=>{e.preventDefault();const payload={title:fields.title.value,description:fields.description.value,status:fields.status.value,priority:fields.priority.value};try{const id=fields.id.value;await request(id?`${api}/${id}`:api,{method:id?'PUT':'POST',body:JSON.stringify(payload)});notify(id?'Tarefa atualizada.':'Tarefa criada.');reset();load();}catch(err){notify(err.message,'error');}});
document.querySelector('#cancel').onclick=reset;document.querySelector('#reload').onclick=load;

// filtros (busca por titulo e status)
const filterForm = document.querySelector('#filters');
const filterTitle = document.querySelector('#filter-title');
const filterStatus = document.querySelector('#filter-status');

function buildQuery() {
  const params = new URLSearchParams();
  if (filterTitle.value.trim()) params.append('title', filterTitle.value.trim());
  if (filterStatus.value) params.append('status', filterStatus.value);
  const query = params.toString();
  return query ? '?' + query : '';
}

filterForm.addEventListener('submit', e => { e.preventDefault(); load(); });
filterStatus.addEventListener('change', load);
document.querySelector('#clear-filters').onclick = () => { filterForm.reset(); load(); };

load();
